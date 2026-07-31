package com.farmwise.alert.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmwise.alert.dto.LatestAlertStateRow;
import com.farmwise.alert.mapper.AlertMapper;
import com.farmwise.alert.model.Alert;
import com.farmwise.alert.model.AlertBlockingState;
import com.farmwise.alert.model.AlertDetectionState;
import com.farmwise.alert.store.AlertStateStore;
import com.farmwise.common.exception.BizException;
import com.farmwise.device.event.SensorReadingsSavedEvent;
import com.farmwise.device.model.SensorReading;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.monitoring.mapper.EnvironmentThresholdMapper;
import com.farmwise.monitoring.mapper.SensorReadingMapper;
import com.farmwise.monitoring.model.EnvironmentThreshold;
import com.farmwise.task.mapper.FarmTaskMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertEvaluationService {
    private static final int CONFIRMATION_COUNT = 3;
    private static final String BATTERY_METRIC = "battery";
    private static final String DESCRIPTION_TEMPLATE = "当前监测值为 %s%s，正常范围为 %s～%s%s";
    private static final String BATTERY_DESCRIPTION_TEMPLATE = "检测到设备电量为 %s%s，低于配置下限 %s%s";
    private static final String SUGGESTION = "请检查传感器状态和现场环境，并根据实际情况及时处理。";
    private static final String BATTERY_SUGGESTION = "请检查该地块设备供电状态，及时充电或更换电池。";

    private enum AlertCondition {
        LOW,
        NORMAL,
        HIGH
    }

    private final EnvironmentThresholdMapper thresholdMapper;
    private final LandMapper landMapper;
    private final AlertStateStore alertStateStore;
    private final AlertMapper alertMapper;
    private final SensorReadingMapper sensorReadingMapper;
    private final FarmTaskMapper farmTaskMapper;

    private final TransactionTemplate transactionTemplate;

    public void evaluate(SensorReadingsSavedEvent event) {
        String landId = event.readings().getFirst().landId();

        Map<String, EnvironmentThreshold> thresholdsByMetric = thresholdMapper.findEnabledByLandId(landId).stream()
                .collect(Collectors.toMap(EnvironmentThreshold::metric, Function.identity()));

        for (SensorReading reading : event.readings()) {
            EnvironmentThreshold threshold = thresholdsByMetric.get(reading.metric());
            if (threshold == null) {
                continue;
            }
            evaluateReading(reading, threshold);
        }
    }

    public void retryRecovery(String alertId, LocalDateTime changedAt) {
        Alert alert = alertMapper.findById(alertId).orElse(null);

        if (alert == null || alert.landId() == null || alert.sourceMetric() == null
                || (!"pending".equals(alert.status()) && !"processing".equals(alert.status()))) {
            return;
        }

        boolean supported = "environment".equals(alert.type())
                || ("device".equals(alert.type())
                        && BATTERY_METRIC.equals(
                                alert.sourceMetric()));

        if (!supported) {
            return;
        }

        AlertBlockingState state = alertStateStore.findBlocking(alert.landId(), alert.sourceMetric()).orElse(null);

        if (state == null || !state.blocking() || !alert.id().equals(state.alertId())) {
            return;
        }

        long remainingAbnormal = alertStateStore.countAbnormals(alert.landId(), alert.sourceMetric());

        if (remainingAbnormal > 0) {
            return;
        }

        boolean finished = transactionTemplate.execute(status -> {
            String landId = landMapper.findIdForUpdate(alert.landId());

            if (landId == null) {
                throw new IllegalArgumentException("预警所属地块不存在");
            }

            long remaining = alertStateStore.countAbnormals(alert.landId(), alert.sourceMetric());

            if (remaining > 0) {
                return false;
            }

            Alert lockedAlert = alertMapper
                    .findForUpdate(alert.id(), alert.type(), alert.landId(), alert.sourceMetric())
                    .orElseThrow(() -> new IllegalStateException("地块预警不存在"));

            return switch (lockedAlert.status()) {
                case "resolved", "ignored" -> true;
                case "pending", "processing" -> {
                    boolean hasActiveTask = farmTaskMapper.existsActiveByAlertId(lockedAlert.id());

                    if (hasActiveTask) {
                        yield false;
                    }

                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

                    int affectedRows = alertMapper.autoResolve(
                            lockedAlert.id(),
                            changedAt,
                            now);

                    if (affectedRows != 1) {
                        throw new BizException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "任务结束后自动解决预警失败");
                    }

                    yield true;
                }
                default -> throw new IllegalArgumentException(
                        "不支持的预警状态：" + lockedAlert.status());
            };
        });

        if (finished) {
            alertStateStore.saveBlocking(alert.landId(), alert.sourceMetric(), AlertBlockingState.initial());
        }
    }

    private void evaluateReading(
            SensorReading reading,
            EnvironmentThreshold threshold) {
        AlertCondition condition = classify(reading.value(), threshold);

        AlertDetectionState state = alertStateStore.findDetection(
                reading.deviceId(),
                reading.metric())
                .orElseGet(() -> rebuildState(reading, threshold));
        applyCondition(reading, threshold, condition, state);
    }

    private void applyCondition(SensorReading reading, EnvironmentThreshold threshold, AlertCondition condition,
            AlertDetectionState state) {
        AlertDetectionState nextState = switch (condition) {
            case NORMAL -> applyNormal(reading, state);
            case LOW, HIGH -> applyAbnormal(reading, threshold, condition, state);
        };

        alertStateStore.saveDetection(reading.deviceId(), reading.metric(), nextState);
    }

    private AlertDetectionState applyAbnormal(SensorReading reading, EnvironmentThreshold threshold,
            AlertCondition condition,
            AlertDetectionState state) {
        if (alertStateStore.isAbnormal(
                reading.landId(),
                reading.metric(),
                reading.deviceId())) {
            return AlertDetectionState.initial();
        }

        int abnormalCount = Math.min(state.abnormalCount() + 1, CONFIRMATION_COUNT);
        if (abnormalCount < CONFIRMATION_COUNT) {
            return new AlertDetectionState(abnormalCount, 0);
        }

        String alertId = createAlert(reading, threshold, condition);

        alertStateStore.saveBlocking(
                reading.landId(),
                reading.metric(),
                new AlertBlockingState(true, alertId));

        return AlertDetectionState.initial();
    }

    private AlertDetectionState applyNormal(SensorReading reading, AlertDetectionState state) {
        boolean confirmedAbnormal = alertStateStore.isAbnormal(reading.landId(), reading.metric(), reading.deviceId());

        if (!confirmedAbnormal) {
            return AlertDetectionState.initial();
        }

        int normalCount = Math.min(state.normalCount() + 1, CONFIRMATION_COUNT);

        if (normalCount < CONFIRMATION_COUNT) {
            return new AlertDetectionState(0, normalCount);
        }

        alertStateStore.removeAbnormal(reading.landId(), reading.metric(), reading.deviceId());

        long remainingAbnormal = alertStateStore.countAbnormals(reading.landId(), reading.metric());

        if (remainingAbnormal == 0) {
            finishLandRecovery(reading);
        }

        return AlertDetectionState.initial();
    }

    private void finishLandRecovery(SensorReading reading) {
        AlertBlockingState state = alertStateStore.findBlocking(reading.landId(), reading.metric())
                .orElseGet(() -> rebuildBlocking(reading));

        if (!state.blocking() || state.alertId() == null) {
            return;
        }

        boolean finished = transactionTemplate.execute(status -> {
            String landId = landMapper.findIdForUpdate(reading.landId());

            if (landId == null) {
                throw new IllegalStateException("预警所属地块不存在");
            }

            long remainingAbnormal = alertStateStore.countAbnormals(landId, reading.metric());

            if (remainingAbnormal > 0) {
                return false;
            }

            Alert alert = alertMapper.findForUpdate(
                    state.alertId(),
                    alertType(reading.metric()),
                    reading.landId(),
                    reading.metric())
                    .orElseThrow(() -> new IllegalStateException("地块预警不存在"));

            return switch (alert.status()) {
                case "resolved", "ignored" -> true;
                case "pending", "processing" -> {
                    boolean hasActiveTask = farmTaskMapper.existsActiveByAlertId(alert.id());
                    if (hasActiveTask) {
                        yield false;
                    }
                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

                    int affectedRows = alertMapper.autoResolve(alert.id(), reading.recordedAt(), now);

                    if (affectedRows != 1) {
                        throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "自动解决预警失败");
                    }

                    yield true;
                }
                default -> throw new IllegalArgumentException("不支持的预警状态：" + alert.status());
            };
        });

        if (finished) {
            alertStateStore.saveBlocking(reading.landId(), reading.metric(), AlertBlockingState.initial());
        }
    }

    private AlertBlockingState rebuildBlocking(SensorReading reading) {
        LatestAlertStateRow latest = alertMapper.findLatestState(
                alertType(reading.metric()),
                reading.landId(),
                reading.metric()).orElse(null);

        AlertBlockingState state;

        if (latest == null) {
            state = AlertBlockingState.initial();
        } else {
            state = switch (latest.status()) {
                case "pending", "processing" ->
                    new AlertBlockingState(true, latest.id());
                case "resolved", "ignored" -> AlertBlockingState.initial();
                default -> throw new IllegalArgumentException("不支持的预警状态：" + latest.status());
            };
        }

        alertStateStore.saveBlocking(reading.landId(), reading.metric(), state);

        return state;
    }

    private String createAlert(SensorReading reading, EnvironmentThreshold threshold, AlertCondition condition) {
        String alertId = transactionTemplate.execute(status -> {
            String landId = landMapper.findIdForUpdate(reading.landId());
            if (landId == null) {
                throw new IllegalStateException("预警信息所属的地块不存在");
            }
            LatestAlertStateRow row = alertMapper
                    .findActiveByLandAndMetric(
                            alertType(reading.metric()),
                            reading.landId(),
                            reading.metric())
                    .orElse(null);
            String id;
            if (row != null) {
                id = row.id();
            } else {
                id = UUID.randomUUID().toString();
                String direction = condition == AlertCondition.LOW ? "偏低" : "偏高";
                boolean batteryAlert = BATTERY_METRIC.equals(reading.metric());
                String title = batteryAlert
                        ? "该地块存在设备电量偏低"
                        : reading.metric() + direction;
                String description = batteryAlert
                        ? BATTERY_DESCRIPTION_TEMPLATE.formatted(
                                reading.value().stripTrailingZeros().toPlainString(),
                                reading.unit(),
                                threshold.minValue().stripTrailingZeros().toPlainString(),
                                reading.unit())
                        : DESCRIPTION_TEMPLATE.formatted(
                                reading.value().stripTrailingZeros().toPlainString(),
                                reading.unit(),
                                threshold.minValue().stripTrailingZeros().toPlainString(),
                                threshold.maxValue().stripTrailingZeros().toPlainString(),
                                reading.unit());

                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                Alert alert = new Alert(
                        id,
                        reading.landId(),
                        alertType(reading.metric()),
                        "medium",
                        title,
                        description,
                        batteryAlert ? BATTERY_SUGGESTION : SUGGESTION,
                        "pending",
                        reading.recordedAt(),
                        reading.deviceId(),
                        reading.metric(),
                        reading.value(),
                        reading.unit(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        now,
                        now);
                if (alertMapper.add(alert) != 1) {
                    throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "自动创建预警失败");
                }
            }

            alertStateStore.addAbnormal(
                    reading.landId(),
                    reading.metric(),
                    reading.deviceId());

            return id;
        });

        if (alertId == null) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "自动创建预警失败");
        }

        return alertId;
    }

    private AlertDetectionState rebuildState(SensorReading reading, EnvironmentThreshold threshold) {
        LatestAlertStateRow latest = alertMapper.findLatestState(
                alertType(reading.metric()),
                reading.landId(),
                reading.metric())
                .orElse(null);

        if (latest == null) {
            alertStateStore.saveBlocking(
                    reading.landId(),
                    reading.metric(),
                    AlertBlockingState.initial());
            return AlertDetectionState.initial();
        }

        return switch (latest.status()) {
            case "pending", "processing" -> rebuildActiveState(reading, latest);

            case "resolved", "ignored" -> rebuildTerminalState(reading, threshold, latest);

            default -> throw new IllegalArgumentException("不支持的预警状态：" + latest.status());
        };
    }

    private AlertDetectionState rebuildActiveState(
            SensorReading reading,
            LatestAlertStateRow latest) {
        alertStateStore.saveBlocking(
                reading.landId(),
                reading.metric(),
                new AlertBlockingState(true, latest.id()));

        if (reading.deviceId().equals(latest.sourceDeviceId())) {
            alertStateStore.addAbnormal(
                    reading.landId(),
                    reading.metric(),
                    reading.deviceId());
        }

        return AlertDetectionState.initial();
    }

    private AlertDetectionState rebuildTerminalState(SensorReading reading, EnvironmentThreshold threshold,
            LatestAlertStateRow latest) {
        if (latest.handledAt() == null) {
            throw new IllegalStateException("终态预警不能没有结束时间");
        }

        List<SensorReading> latestReadings = sensorReadingMapper.findForAlertStateRebuild(reading.landId(),
                reading.deviceId(), reading.metric(), latest.handledAt(), reading.recordedAt());

        boolean confirmedAbnormal = reading.deviceId().equals(latest.sourceDeviceId())
                || alertStateStore.isAbnormal(
                        reading.landId(),
                        reading.metric(),
                        reading.deviceId());
        int abnormalCount = 0;
        int normalCount = 0;

        for (SensorReading oldReading : latestReadings) {
            if (confirmedAbnormal) {
                if (isNormal(oldReading.value(), threshold)) {
                    normalCount++;
                    if (normalCount >= CONFIRMATION_COUNT) {
                        confirmedAbnormal = false;
                        normalCount = 0;
                    }
                } else {
                    normalCount = 0;
                }
            } else {
                if (!isNormal(oldReading.value(), threshold)) {
                    abnormalCount = Math.min(abnormalCount + 1, CONFIRMATION_COUNT - 1);
                } else {
                    abnormalCount = 0;
                }
            }
        }

        if (confirmedAbnormal) {
            alertStateStore.addAbnormal(
                    reading.landId(),
                    reading.metric(),
                    reading.deviceId());
        } else {
            alertStateStore.removeAbnormal(
                    reading.landId(),
                    reading.metric(),
                    reading.deviceId());
        }

        AlertBlockingState blockingState = alertStateStore.countAbnormals(
                reading.landId(),
                reading.metric()) > 0
                        ? new AlertBlockingState(true, latest.id())
                        : AlertBlockingState.initial();
        alertStateStore.saveBlocking(reading.landId(), reading.metric(), blockingState);

        return new AlertDetectionState(abnormalCount, normalCount);
    }

    private boolean isNormal(BigDecimal value, EnvironmentThreshold threshold) {
        return value.compareTo(threshold.minValue()) >= 0 && value.compareTo(threshold.maxValue()) <= 0;
    }

    private String alertType(String metric) {
        return BATTERY_METRIC.equals(metric) ? "device" : "environment";
    }

    private AlertCondition classify(
            BigDecimal value,
            EnvironmentThreshold threshold) {
        if (value.compareTo(threshold.minValue()) < 0) {
            return AlertCondition.LOW;
        }

        if (value.compareTo(threshold.maxValue()) > 0) {
            return AlertCondition.HIGH;
        }

        return AlertCondition.NORMAL;
    }
}

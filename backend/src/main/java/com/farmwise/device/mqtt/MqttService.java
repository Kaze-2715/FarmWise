package com.farmwise.device.mqtt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmwise.device.capability.DeviceMetricCapabilities;
import com.farmwise.device.event.SensorReadingsSavedEvent;
import com.farmwise.device.event.SoilMoistureReportedEvent;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.mapper.TelemetryMapper;
import com.farmwise.device.model.Device;
import com.farmwise.device.model.SensorReading;
import com.farmwise.irrigation.mapper.IrrigationMapper;
import com.farmwise.irrigation.model.IrrigationRecord;
import com.farmwise.irrigation.mqtt.IrrigationAck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {
    private static final String TOPIC_FILTER = "farmwise/v1/devices/+/";

    private static final Map<String, String> UNIT_BY_METRIC = Map.of("soil_moisture",
            "%",
            "air_temperature",
            "℃",
            "air_humidity",
            "%",
            "light",
            "lux",
            "soil_ph",
            "pH",
            "battery",
            "%");

    private final ObjectMapper objectMapper;
    private final DeviceMapper deviceMapper;
    private final TelemetryMapper telemetryMapper;
    private final IrrigationMapper irrigationMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final MqttProperties properties;

    private final TransactionTemplate transaction;

    public void processTelemetry(String topic, byte[] payload) {
        String deviceId = extractDeviceId(topic, "telemetry");
        TelemetryPayload telemetry = parsePayload(payload, TelemetryPayload.class);
        Device device = deviceMapper.findById(deviceId).orElseThrow(
                () -> new IllegalArgumentException("设备不存在"));
        validateTelemetry(device, telemetry);
        transaction.executeWithoutResult(transactionStatus -> saveTelemetry(device, telemetry));
    }

    public void processStatus(String topic, byte[] payload) {
        String deviceId = extractDeviceId(topic, "status");

        StatusPayload statusPayload = parsePayload(payload, StatusPayload.class);

        if (statusPayload == null
                || !("online".equals(statusPayload.status())
                        || "offline".equals(statusPayload.status()))) {
            throw new IllegalArgumentException("status 只能是 online 或 offline");
        }

        int affectedRows = deviceMapper.updateStatus(deviceId, statusPayload.status());

        if (affectedRows == 0) {
            throw new IllegalArgumentException("设备不存在：" + deviceId);
        }
    }

    public void processAck(String topic, byte[] payload) {
        String deviceId = extractDeviceId(topic, "command-acks");

        IrrigationAck ack = parsePayload(payload, IrrigationAck.class);

        validateAck(ack);

        transaction.executeWithoutResult(status -> applyAck(deviceId, ack));
    }

    @Scheduled(fixedDelayString = "${farmwise.mqtt.offline-scan-interval-milliseconds:30000}")
    public void markTimedOutDevicesOffline() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(properties.offlineTimeoutSeconds());

        int affectedRows = deviceMapper.markTimedOutDevicesOffline(cutoff);

        if (affectedRows > 0) {
            log.info("已将 {} 台超时设备标记为离线", affectedRows);
        }
    }

    private void saveTelemetry(Device device, TelemetryPayload telemetry) {
        LocalDateTime reportedAt = LocalDateTime.ofInstant(telemetry.reportedAt(), ZoneOffset.UTC);

        try {
            telemetryMapper.recordMessage(
                    device.id(), telemetry.messageId().toString(), reportedAt);
        } catch (DuplicateKeyException exception) {
            return;
        }

        List<SensorReading> readings = Stream.concat(telemetry.readings().stream().map(
                reading -> new SensorReading(
                        device.id(),
                        device.landId(),
                        reportedAt,
                        reading.metric(),
                        UNIT_BY_METRIC.get(reading.metric()),
                        reading.value())),
                Optional.ofNullable(telemetry.battery())
                        .stream()
                        .map(battery -> new SensorReading(
                                device.id(),
                                device.landId(),
                                reportedAt,
                                "battery",
                                UNIT_BY_METRIC.get("battery"),
                                battery)))
                .toList();
        telemetryMapper.insertReadings(readings);

        deviceMapper.updateStateFromTelemetry(device.id(), telemetry.battery(), reportedAt);

        if (device.landId() != null) {
            eventPublisher.publishEvent(
                new SensorReadingsSavedEvent(telemetry.messageId().toString(), readings)
            );
        }

        if (device.landId() != null) {
            telemetry.readings()
                    .stream()
                    .filter(reading -> "soil_moisture".equals(reading.metric()))
                    .findFirst()
                    .ifPresent(
                            reading -> eventPublisher.publishEvent(new SoilMoistureReportedEvent(
                                    telemetry.messageId().toString(),
                                    device.id(),
                                    device.landId(),
                                    reading.value(),
                                    telemetry.reportedAt())));
        }
    }

    private void applyAck(String deviceId, IrrigationAck ack) {
        String recordId = ack.recordId();

        IrrigationRecord record = irrigationMapper.findByIdForUpdate(recordId).orElseThrow(
                () -> new IllegalArgumentException("不存在对应的 irrigation record"));

        if (!deviceId.equals(record.controllerDeviceId())) {
            throw new IllegalArgumentException(
                    "irrigationAck 中的 deviceId 与 Irrigation Record 中的 deviceId 不一致");
        }

        if (record.status().equals(ack.status())) {
            return;
        }

        if ("completed".equals(record.status()) || "failed".equals(record.status())) {
            return;
        }

        LocalDateTime occurredAt = LocalDateTime.ofInstant(ack.occurredAt(), ZoneOffset.UTC);

        int affectedRows = switch (ack.status()) {
            case "running" -> irrigationMapper.markRunning(recordId, occurredAt);
            case "completed" -> irrigationMapper.markCompleted(recordId, occurredAt.minusMinutes(ack.duration()),
                    occurredAt, ack.duration(), ack.waterUsage());
            case "failed" -> irrigationMapper.markFailed(recordId, occurredAt);
            default -> throw new IllegalArgumentException("不支持的回执状态");
        };

        if (affectedRows == 0) {
            throw new IllegalArgumentException("灌溉记录状态更新失败, recordId=" + recordId);
        }
    }

    private <T> T parsePayload(byte[] payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("MQTT 消息不是 %s 类型的合法 JSON".formatted(clazz.getSimpleName()), exception);
        }
    }

    private void validateAck(IrrigationAck ack) {
        if (ack == null) {
            throw new IllegalArgumentException("ack 对象不应为空");
        }

        if (ack.recordId() == null) {
            throw new IllegalArgumentException("recordId 不能为空");
        }

        if (ack.occurredAt() == null) {
            throw new IllegalArgumentException("occurredAt 不能为空");
        }

        String action = ack.action();
        if (action == null || !("start".equals(action) || "stop".equals(action))) {
            throw new IllegalArgumentException("action 只能为 start/stop");
        }
        String status = ack.status();
        if (status == null || !("running".equals(status) || "completed".equals(status)
                || "failed".equals(status))) {
            throw new IllegalArgumentException("status 只能是 running/completed/failed");
        }

        if ("stop".equals(action) && "running".equals(status)) {
            throw new IllegalArgumentException("stop + running 是非法组合");
        }

        if ("completed".equals(status) && (ack.duration() == null || ack.duration() < 0)) {
            throw new IllegalArgumentException("duration 必须 ≥ 0");
        }

        if ("failed".equals(status) && (ack.reason() == null || ack.reason().isBlank())) {
            throw new IllegalArgumentException("failed 回执必须提供非空的 reason");
        }

        if (ack.waterUsage() != null && ack.waterUsage().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("water usage 必须 ≥ 0");
        }
    }

    private void validateTelemetry(Device device, TelemetryPayload telemetry) {
        if (telemetry.messageId() == null) {
            throw new IllegalArgumentException("messageId 不能为空");
        }
        if (telemetry.reportedAt() == null) {
            throw new IllegalArgumentException("reportedAt 不能为空");
        }
        if (telemetry.reportedAt().isAfter(Instant.now().plusSeconds(300))) {
            throw new IllegalArgumentException("报告时间不能超过服务器时间 5 分钟");
        }
        if (telemetry.battery() != null
                && (telemetry.battery().compareTo(BigDecimal.ZERO) < 0
                        || telemetry.battery().compareTo(new BigDecimal(100)) > 0)) {
            throw new IllegalArgumentException("电量必须在 0 - 100 之间");
        }
        if (telemetry.readings() == null || telemetry.readings().isEmpty()) {
            throw new IllegalArgumentException("readings 不能为空");
        }

        Set<String> allowedMetrics = DeviceMetricCapabilities.sensorMetricsForDeviceType(device.deviceType());

        if (allowedMetrics == null) {
            throw new IllegalArgumentException("不支持的设备类型：" + device.deviceType());
        }

        Set<String> receivedMetrics = new HashSet<>();

        for (TelemetryPayload.Reading reading : telemetry.readings()) {
            if (reading == null || reading.metric() == null || reading.metric().isBlank()
                    || reading.value() == null) {
                throw new IllegalArgumentException("reading 的 metric 和 value 都不能为空");
            }

            if (!allowedMetrics.contains(reading.metric())) {
                throw new IllegalArgumentException("设备类型不支持指标");
            }

            if (!receivedMetrics.add(reading.metric())) {
                throw new IllegalArgumentException(
                        "同一消息不能重复上报指标: " + reading.metric());
            }
        }
    }

    private String extractDeviceId(String topic, String type) {
        String[] levels = topic.split("/", -1);
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("topic 的类型不正确");
        }

        type = type.strip();

        if (levels.length != 5 || !"farmwise".equals(levels[0]) || !"v1".equals(levels[1])
                || !"devices".equals(levels[2]) || !type.equals(levels[4])) {
            throw new IllegalArgumentException(
                    "topic 格式不正确，正确格式 %s".formatted(TOPIC_FILTER + type));
        }

        final UUID deviceId;

        try {
            deviceId = UUID.fromString(levels[3]);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("设备 ID 不是合法 UUID");
        }

        if (!deviceId.toString().equalsIgnoreCase(levels[3])) {
            throw new IllegalArgumentException("设备 UUID 不是标准 UUID");
        }

        return deviceId.toString();
    }
}

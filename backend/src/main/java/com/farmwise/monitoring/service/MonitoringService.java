package com.farmwise.monitoring.service;

import static com.farmwise.common.util.ValidationUtil.validateOptional;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.common.exception.BizException;
import com.farmwise.device.capability.DeviceMetricCapabilities;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.model.SensorReading;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.monitoring.dto.CreateEnvironmentThresholdRequest;
import com.farmwise.monitoring.dto.EnvironmentThresholdResponse;
import com.farmwise.monitoring.dto.EnvironmentThresholdRow;
import com.farmwise.monitoring.dto.SensorReadingResponse;
import com.farmwise.monitoring.dto.UpdateEnvironmentThresholdRequest;
import com.farmwise.monitoring.mapper.EnvironmentThresholdMapper;
import com.farmwise.monitoring.mapper.SensorReadingMapper;
import com.farmwise.monitoring.model.EnvironmentThreshold;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoringService {
    private static final Duration MAX_SENSOR_READING_RANGE = Duration.ofHours(24);

    private final SensorReadingMapper sensorReadingMapper;
    private final EnvironmentThresholdMapper thresholdMapper;
    private final LandMapper landMapper;
    private final DeviceMapper deviceMapper;

    @Transactional
    public void deleteEnvironmentThreshold(
            String userId,
            String landId,
            String metric) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));
        metric = validateMetric(metric);

        int affectedRows = thresholdMapper.deleteByLandIdAndMetric(landId, metric);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.NOT_FOUND, "环境阈值不存在");
        }
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> listSensorReadings(
            String userId,
            String landId,
            String metric,
            LocalDateTime startAt,
            LocalDateTime endAt) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        metric = validateOptional(metric);

        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        if (metric != null && !DeviceMetricCapabilities.isSupportedMetric(metric)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的指标类型");
        }

        LocalDateTime effectiveEndAt = endAt != null
                ? endAt
                : LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime effectiveStartAt = startAt != null
                ? startAt
                : effectiveEndAt.minus(MAX_SENSOR_READING_RANGE);

        if (effectiveStartAt.isAfter(effectiveEndAt)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "起始时间不能晚于结束时间");
        }

        if (Duration.between(effectiveStartAt, effectiveEndAt)
                .compareTo(MAX_SENSOR_READING_RANGE) > 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "传感器数据查询范围不能超过 24 小时");
        }

        List<SensorReading> readings = sensorReadingMapper.findByLandAndMetricAndTime(
                landId,
                metric,
                effectiveStartAt,
                effectiveEndAt);

        return readings.stream().map(SensorReadingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> listLatestSensorReadings(
            String userId,
            String landId) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        return sensorReadingMapper.findLatestByLandId(landId).stream()
                .map(row -> new SensorReadingResponse(
                        row.deviceId(),
                        row.landId(),
                        row.recordedAt(),
                        row.metric(),
                        row.unit(),
                        row.value()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnvironmentThresholdResponse> listEnvironmentThresholds(
            String userId,
            String landId) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));
        List<EnvironmentThresholdRow> thresholds = thresholdMapper.findAllByLandId(landId);

        return thresholds.stream().map(EnvironmentThresholdResponse::from).toList();
    }

    @Transactional
    public EnvironmentThresholdResponse createEnvironmentThreshold(
            String userId,
            String landId,
            CreateEnvironmentThresholdRequest request) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        String metric = validateMetric(request.metric());
        validateThresholdRange(request.min(), request.max());
        validateBatteryThreshold(metric, request.min(), request.max());
        validateMetricAvailable(landId, metric);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        EnvironmentThreshold threshold = new EnvironmentThreshold(landId, metric, request.min(), request.max(),
                request.enabled(), userId, now);

        try {
            int affectedRows = thresholdMapper.addThreshold(threshold);
            if (affectedRows != 1) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "添加监测规则失败");
            }
        } catch (DuplicateKeyException exception) {
            throw new BizException(HttpStatus.CONFLICT, "该地块已经存在该指标的阈值");
        }

        EnvironmentThresholdRow row = thresholdMapper.findByMetricAndLandId(landId, metric);

        return EnvironmentThresholdResponse.from(row);
    }

    @Transactional
    public EnvironmentThresholdResponse updateEnvironmentThreshold(
            String userId,
            String landId,
            String metric,
            UpdateEnvironmentThresholdRequest request) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));
        metric = validateMetric(metric);
        validateThresholdRange(request.min(), request.max());
        validateBatteryThreshold(metric, request.min(), request.max());

        if (request.enabled()) {
            validateMetricAvailable(landId, metric);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int affectedRows = thresholdMapper.updateThreshold(
                landId,
                metric,
                request.min(),
                request.max(),
                request.enabled(),
                now);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.NOT_FOUND, "环境阈值不存在");
        }

        EnvironmentThresholdRow row = thresholdMapper.findByMetricAndLandId(landId, metric);
        return EnvironmentThresholdResponse.from(row);
    }

    private String validateMetric(String metric) {
        metric = validateOptional(metric);
        if (!DeviceMetricCapabilities.isSupportedMetric(metric)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的监测指标类型: " + metric);
        }
        return metric;
    }

    private void validateThresholdRange(
            BigDecimal min,
            BigDecimal max) {
        if (min.compareTo(max) >= 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "阈值下限必须小于阈值上限");
        }
    }

    private void validateBatteryThreshold(String metric, BigDecimal min, BigDecimal max) {
        if ("battery".equals(metric)
                && (min.compareTo(BigDecimal.ZERO) < 0
                        || max.compareTo(BigDecimal.valueOf(100)) != 0)) {
            throw new BizException(
                    HttpStatus.BAD_REQUEST,
                    "电量阈值下限不能小于 0，上限必须为 100");
        }
    }

    private void validateMetricAvailable(String landId, String metric) {
        boolean metricAvailable = deviceMapper.findTypesByLandId(landId)
                .stream()
                .anyMatch(deviceType -> DeviceMetricCapabilities.supportsMetric(deviceType, metric));
        if (!metricAvailable) {
            throw new BizException(HttpStatus.BAD_REQUEST, "地块不存在支持监测该指标的设备");
        }
    }

}

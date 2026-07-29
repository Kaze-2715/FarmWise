package com.farmwise.monitoring.service;

import java.math.BigDecimal;
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
    private final SensorReadingMapper sensorReadingMapper;
    private final EnvironmentThresholdMapper thresholdMapper;
    private final LandMapper landMapper;
    private final DeviceMapper deviceMapper;

    @Transactional
    public void deleteEnvironmentThreshold(
            String userId,
            String landId,
            String metric) {
        landId = normalize(landId);
        validateLand(landId, userId);
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
        landId = normalize(landId);
        metric = normalize(metric);

        validateLand(landId, userId);

        if (metric != null && !DeviceMetricCapabilities.isSupportedMetric(metric)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的指标类型");
        }

        if (startAt != null && endAt != null && startAt.isAfter(endAt)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "起始时间不能晚于结束时间");
        }

        List<SensorReading> readings = sensorReadingMapper.findByLandAndMetricAndTime(landId, metric, startAt, endAt);

        return readings.stream().map(SensorReadingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EnvironmentThresholdResponse> listEnvironmentThresholds(
            String userId,
            String landId) {
        landId = normalize(landId);
        validateLand(landId, userId);
        List<EnvironmentThresholdRow> thresholds = thresholdMapper.findAllByLandId(landId);

        return thresholds.stream().map(EnvironmentThresholdResponse::from).toList();
    }

    @Transactional
    public EnvironmentThresholdResponse createEnvironmentThreshold(
            String userId,
            String landId,
            CreateEnvironmentThresholdRequest request) {
        landId = normalize(landId);
        validateLand(landId, userId);

        String metric = validateMetric(request.metric());
        validateThresholdRange(request.min(), request.max());
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
        landId = normalize(landId);
        validateLand(landId, userId);
        metric = validateMetric(metric);
        validateThresholdRange(request.min(), request.max());

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
        metric = normalize(metric);
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

    private void validateMetricAvailable(String landId, String metric) {
        boolean metricAvailable = deviceMapper.findTypesByLandId(landId)
                .stream()
                .anyMatch(deviceType -> DeviceMetricCapabilities.supportsMetric(deviceType, metric));
        if (!metricAvailable) {
            throw new BizException(HttpStatus.BAD_REQUEST, "地块不存在支持监测该指标的设备");
        }
    }

    private void validateLand(String landId, String userId) {
        if (landId == null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "地块 ID 不能为空");
        }
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在或不属于当前用户"));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        value = value.strip();

        return value.isBlank() ? null : value;
    }
}

package com.farmwise.alert.service;

import static com.farmwise.common.util.ValidationUtil.validateFilter;
import static com.farmwise.common.util.ValidationUtil.validateOptional;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.alert.dto.AlertQueryRow;
import com.farmwise.alert.dto.AlertResponse;
import com.farmwise.alert.dto.AlertSourceRequest;
import com.farmwise.alert.dto.CreateAlertRequest;
import com.farmwise.alert.dto.IgnoreAlertRequest;
import com.farmwise.alert.dto.ResolveAlertRequest;
import com.farmwise.alert.dto.StartAlertRequest;
import com.farmwise.alert.mapper.AlertMapper;
import com.farmwise.alert.model.Alert;
import com.farmwise.common.exception.BizException;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.model.Device;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.task.mapper.FarmTaskMapper;
import com.farmwise.task.model.FarmTask;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private static final Set<String> TYPES = Set.of("environment", "device", "pest");
    private static final Set<String> SEVERITIES = Set.of("low", "medium", "high");
    private static final Set<String> STATUSES = Set.of("pending", "processing", "resolved", "ignored");
    private static final Set<String> TASK_TYPES = Set.of(
            "irrigation",
            "fertilization",
            "pesticide",
            "weeding",
            "inspection",
            "harvest",
            "other");
    private static final Set<String> PRIORITIES = Set.of("low", "medium", "high");
    private static final Set<String> METRICS = Set.of(
            "soil_moisture",
            "air_temperature",
            "air_humidity",
            "light",
            "soil_ph",
            "battery");

    private final AlertMapper alertMapper;
    private final LandMapper landMapper;
    private final DeviceMapper deviceMapper;
    private final FarmTaskMapper taskMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<AlertResponse> listAlerts(
            String userId,
            String landId,
            String type,
            String severity,
            String status) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        type = validateFilter(type, TYPES, "不支持的预警类型");
        severity = validateFilter(severity, SEVERITIES, "不支持的预警等级");
        status = validateFilter(status, STATUSES, "不支持的预警状态");

        return alertMapper.findAllByConditions(landId, type, severity, status)
                .stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Transactional
    public AlertResponse createAlert(String userId, CreateAlertRequest request) {
        String landId = validateRequired(request.landId(), "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        String type = validateRequired(request.type(), "预警类型不能为空");
        validateFilter(type, TYPES, "不支持的预警类型");

        String severity = validateRequired(request.severity(), "预警等级不能为空");
        validateFilter(severity, SEVERITIES, "不支持的预警等级");

        String title = validateRequired(request.title(), "预警标题不能为空");
        String description = validateRequired(request.description(), "预警描述不能为空");
        String suggestion = validateRequired(request.suggestion(), "处理建议不能为空");

        if (request.occurredAt() == null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "预警发生时间不能为空");
        }
        if (request.source() == null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "预警来源不能为空");
        }

        AlertSourceRequest source = request.source();
        String sourceDeviceId = validateOptional(source.deviceId());
        String sourceMetric = validateFilter(source.metric(), METRICS, "不支持的预警来源指标");
        String sourceUnit = validateOptional(source.unit());

        if (sourceDeviceId != null) {
            Device sourceDevice = deviceMapper.findByIdAndOwnerId(sourceDeviceId, userId)
                    .orElseThrow(() -> new BizException(
                            HttpStatus.NOT_FOUND,
                            "预警来源设备不存在或不属于当前用户"));
            if (!landId.equals(sourceDevice.landId())) {
                throw new BizException(HttpStatus.BAD_REQUEST, "预警来源设备不属于指定地块");
            }
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Alert alert = new Alert(
                UUID.randomUUID().toString(),
                landId,
                type,
                severity,
                title,
                description,
                suggestion,
                "pending",
                request.occurredAt(),
                sourceDeviceId,
                sourceMetric,
                source.value(),
                sourceUnit,
                null,
                null,
                null,
                null,
                null,
                now,
                now);

        if (alertMapper.add(alert) != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "创建预警失败");
        }

        return AlertResponse.from(alert);
    }

    @Transactional
    public AlertResponse startAlert(String userId, String alertId, StartAlertRequest request) {
        Alert alert = alertMapper.findByIdForUpdate(alertId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警不存在"));
        landMapper.findByIdAndOwnerId(alert.landId(), userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警所属地块不存在或不属于该用户"));
        if (!"pending".equals(alert.status())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "只有 pending 状态可以启动");
        }
        if (request.createTask()) {
            String taskType = validateRequired(request.taskType(), "任务类型不能为空");
            validateFilter(taskType, TASK_TYPES, "不支持的任务类型");

            String priority = validateRequired(request.priority(), "优先级不能为空");
            validateFilter(priority, PRIORITIES, "不支持的任务优先级");

            String assigneeId = validateRequired(request.assigneeId(), "任务执行人 ID 不能为空");
            User assignee = userMapper.findById(assigneeId)
                    .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "任务执行人不存在"));
            if (!"active".equals(assignee.status())) {
                throw new BizException(HttpStatus.BAD_REQUEST, "任务执行人账号已被禁用");
            }

            LocalDateTime ddl = request.deadline();
            if (ddl == null) {
                throw new BizException(HttpStatus.BAD_REQUEST, "任务截止日期不能为空");
            }
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            String id = UUID.randomUUID().toString();
            int affectedRows = taskMapper
                    .addTask(new FarmTask(id, alert.landId(), "alert", alert.id(), taskType, alert.title(),
                            alert.description(), priority, "pending", assigneeId, ddl, now, null, "", "", now));
            if (affectedRows != 1) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "创建农事任务失败");
            }
        }
        int affectedRows = alertMapper.startIfPending(alertId);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "启动预警失败");
        }
        AlertQueryRow row = alertMapper.findRowById(alertId);
        return AlertResponse.from(row);
    }

    @Transactional
    public AlertResponse resolveAlert(String userId, String alertId, ResolveAlertRequest request) {
        Alert alert = alertMapper.findByIdForUpdate(alertId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警不存在"));
        landMapper.findByIdAndOwnerId(alert.landId(), userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警所属地块不存在或不属于该用户"));
        if (!"processing".equals(alert.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有处理中的预警可以完成");
        }

        boolean existsActiveTask = taskMapper.existsActiveByAlertId(alertId);

        if (existsActiveTask) {
            throw new BizException(HttpStatus.CONFLICT, "该预警存在关联的农事任务");
        }

        String measure = validateRequired(request.measure(), "处理措施不能为空");
        String result = validateRequired(request.result(), "处理结果不能为空");
        String remark = validateOptional(request.remark());
        LocalDateTime handledAt = request.handledAt();
        if (handledAt == null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "处理时间不能为空");
        }
        if (handledAt.isBefore(alert.occurredAt())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "处理时间不能早于预警生成时间");
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int affectedRows = alertMapper.resolveIfProcessing(alertId, measure, handledAt, result, remark, userId, now);
        if (affectedRows != 1) {
            throw new BizException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "解决预警失败");
        }

        AlertQueryRow row = alertMapper.findRowById(alertId);
        return AlertResponse.from(row);
    }

    @Transactional
    public AlertResponse ignoreAlert(String userId, String alertId, IgnoreAlertRequest request) {
        Alert alert = alertMapper.findByIdForUpdate(alertId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警不存在"));
        landMapper.findByIdAndOwnerId(alert.landId(), userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "预警所属地块不存在或不属于该用户"));
        if (!"pending".equals(alert.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有待处理预警可以忽略");
        }

        String remark = validateRequired(request.remark(), "忽略原因不能为空");
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int affectedRows = alertMapper.ignoreIfPending(
                alertId,
                remark,
                userId,
                now,
                now);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "忽略预警失败");
        }

        AlertQueryRow row = alertMapper.findRowById(alertId);
        return AlertResponse.from(row);
    }
}

package com.farmwise.ai.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.farmwise.ai.dto.ReferenceResponse;
import com.farmwise.alert.mapper.AlertMapper;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.irrigation.dto.IrrigationConfigControllerRow;
import com.farmwise.irrigation.mapper.IrrigationMapper;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.land.model.Land;
import com.farmwise.monitoring.mapper.EnvironmentThresholdMapper;
import com.farmwise.monitoring.mapper.SensorReadingMapper;
import com.farmwise.planting.mapper.PlantingPlanMapper;
import com.farmwise.task.mapper.FarmTaskMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdvisorTools {
    private static final Map<String, String> METRIC_LABELS = Map.of(
            "soil_moisture", "土壤湿度",
            "air_temperature", "空气温度",
            "air_humidity", "空气湿度",
            "light", "光照强度",
            "soil_ph", "土壤 pH",
            "battery", "设备电量");

    private static final Map<String, String> METRIC_UNITS = Map.of(
            "soil_moisture", "%",
            "air_temperature", "℃",
            "air_humidity", "%",
            "light", "lx",
            "soil_ph", "",
            "battery", "%");

    private final EnvironmentThresholdMapper thresholdMapper;
    private final SensorReadingMapper sensorReadingMapper;
    private final LandMapper landMapper;
    private final PlantingPlanMapper plantingPlanMapper;
    private final DeviceMapper deviceMapper;
    private final AlertMapper alertMapper;
    private final FarmTaskMapper farmTaskMapper;
    private final IrrigationMapper irrigationMapper;

    @Tool(name = "get_land_profile", description = "查询当前咨询地块的名称、类型、面积、作物、状态和位置")
    public LandProfileData getLandProfile(ToolContext toolContext) {
        String userId = requireUserId(toolContext);
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        Land land = landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new IllegalStateException("AI 工具无法访问当前咨询地块"));

        collector.add(new ReferenceResponse(
                "land",
                land.id(),
                land.name(),
                land.crop(),
                ""));

        return new LandProfileData(
                land.name(),
                land.landType(),
                land.area(),
                land.crop(),
                land.status(),
                land.location(),
                land.updatedAt());
    }

    @Tool(name = "get_planting_plans", description = "查询当前咨询地块处于待开始、播种期或生长中的种植计划")
    public List<PlantingPlanData> getPlantingPlans(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return plantingPlanMapper.findActiveByLandId(landId).stream()
                .map(plan -> {
                    collector.add(new ReferenceResponse(
                            "plantingPlan",
                            plan.id(),
                            plan.planName(),
                            plan.status(),
                            ""));
                    return new PlantingPlanData(
                            plan.id(),
                            plan.planName(),
                            plan.cropType(),
                            plan.area(),
                            plan.plantingDate(),
                            plan.expectedHarvestDate(),
                            plan.status(),
                            plan.remark(),
                            plan.updatedAt());
                })
                .toList();
    }

    @Tool(name = "get_devices", description = "查询当前咨询地块的设备名称、类型、在线状态、电量和最后上报时间")
    public List<DeviceData> getDevices(ToolContext toolContext) {
        String userId = requireUserId(toolContext);
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return deviceMapper.findAllByOwnerId(userId, landId, null, null, null).stream()
                .map(device -> {
                    collector.add(new ReferenceResponse(
                            "device",
                            device.id(),
                            device.name(),
                            device.status(),
                            ""));
                    return new DeviceData(
                            device.id(),
                            device.name(),
                            device.deviceType(),
                            device.status(),
                            device.battery(),
                            device.lastReportedAt(),
                            device.model());
                })
                .toList();
    }

    @Tool(name = "get_environment_thresholds", description = "查询当前咨询地块已经启用的环境指标适宜范围，用于判断监测值是否正常")
    public List<EnvironmentThresholdData> getEnvironmentThresholds(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return thresholdMapper.findEnabledByLandId(landId).stream()
                .map(threshold -> {
                    String metric = threshold.metric();
                    String label = METRIC_LABELS.getOrDefault(metric, metric);
                    String unit = METRIC_UNITS.getOrDefault(metric, "");
                    collector.add(new ReferenceResponse(
                            "environmentThreshold",
                            landId + ":" + metric,
                            "适宜" + label,
                            threshold.minValue() + "～" + threshold.maxValue(),
                            unit));
                    return new EnvironmentThresholdData(
                            metric,
                            label,
                            threshold.minValue(),
                            threshold.maxValue(),
                            unit,
                            threshold.updatedAt());
                })
                .toList();
    }

    @Tool(name = "get_latest_sensor_readings", description = "查询当前咨询地块各设备、各指标的最新监测值及采集时间")
    public List<LatestSensorReadingData> getLatestSensorReadings(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return sensorReadingMapper.findLatestByLandId(landId).stream()
                .map(reading -> {
                    String metric = reading.metric();
                    String label = METRIC_LABELS.getOrDefault(metric, metric);

                    collector.add(new ReferenceResponse(
                            "sensorReading",
                            reading.deviceId() + ":" + metric,
                            reading.deviceName() + "的" + label,
                            reading.value(),
                            reading.unit()));
                    return new LatestSensorReadingData(
                            reading.deviceId(),
                            reading.deviceName(),
                            metric,
                            label,
                            reading.value(),
                            reading.unit(),
                            reading.recordedAt());
                })
                .toList();
    }

    @Tool(name = "get_active_alerts", description = "查询当前咨询地块所有待处理或处理中的预警")
    public List<ActiveAlertData> getActiveAlerts(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return alertMapper.findActiveByLandId(landId).stream()
                .map(alert -> {
                    Object referenceValue = alert.sourceValue() == null
                            ? alert.status()
                            : alert.sourceValue();
                    String referenceUnit = alert.sourceValue() == null || alert.sourceUnit() == null
                            ? ""
                            : alert.sourceUnit();
                    collector.add(new ReferenceResponse(
                            "alert",
                            alert.id(),
                            alert.title(),
                            referenceValue,
                            referenceUnit));
                    return new ActiveAlertData(
                            alert.id(),
                            alert.type(),
                            alert.severity(),
                            alert.title(),
                            alert.description(),
                            alert.suggestion(),
                            alert.status(),
                            alert.occurredAt(),
                            alert.sourceMetric(),
                            alert.sourceValue(),
                            alert.sourceUnit());
                })
                .toList();
    }

    @Tool(name = "get_active_farm_tasks", description = "查询当前咨询地块所有待处理或进行中的农事任务，按截止时间优先返回")
    public List<ActiveFarmTaskData> getActiveFarmTasks(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return farmTaskMapper.findActiveByLandId(landId).stream()
                .map(task -> {
                    collector.add(new ReferenceResponse(
                            "farmTask",
                            task.id(),
                            task.title(),
                            task.status(),
                            ""));
                    return new ActiveFarmTaskData(
                            task.id(),
                            task.sourceType(),
                            task.sourceId(),
                            task.taskType(),
                            task.title(),
                            task.description(),
                            task.priority(),
                            task.status(),
                            task.assigneeId(),
                            task.deadline());
                })
                .toList();
    }

    @Tool(name = "get_irrigation_configs", description = "查询当前咨询地块的灌溉配置、启用状态、触发阈值和关联控制器")
    public List<IrrigationConfigData> getIrrigationConfigs(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);
        Map<String, List<String>> controllerIdsByConfigId = new HashMap<>();

        for (IrrigationConfigControllerRow controllerRow : irrigationMapper
                .findConfigControllerRowsByLandId(landId)) {
            controllerIdsByConfigId
                    .computeIfAbsent(controllerRow.configId(), ignored -> new ArrayList<>())
                    .add(controllerRow.controllerDeviceId());
        }

        return irrigationMapper.findConfigRowsByLandId(landId).stream()
                .map(config -> {
                    collector.add(new ReferenceResponse(
                            "irrigationConfig",
                            config.id(),
                            config.name(),
                            config.enabled() ? "已启用" : "已停用",
                            ""));
                    return new IrrigationConfigData(
                            config.id(),
                            config.name(),
                            config.mode(),
                            config.enabled(),
                            config.triggerMoisture(),
                            config.targetMoisture(),
                            config.defaultDuration(),
                            controllerIdsByConfigId.getOrDefault(config.id(), List.of()),
                            config.updatedAt());
                })
                .toList();
    }

    @Tool(name = "get_recent_irrigation_records", description = "查询当前咨询地块最近 10 条灌溉执行记录")
    public List<IrrigationRecordData> getRecentIrrigationRecords(ToolContext toolContext) {
        String landId = requireLandId(toolContext);
        ReferenceCollector collector = requireCollector(toolContext);

        return irrigationMapper.findRecentRecordsByLandId(landId).stream()
                .map(record -> {
                    collector.add(new ReferenceResponse(
                            "irrigationRecord",
                            record.id(),
                            record.source() + "灌溉",
                            record.status(),
                            ""));
                    return new IrrigationRecordData(
                            record.id(),
                            record.controllerDeviceId(),
                            record.source(),
                            record.status(),
                            record.startedAt(),
                            record.endedAt(),
                            record.plannedDuration(),
                            record.duration(),
                            record.waterUsage(),
                            record.triggerReason());
                })
                .toList();
    }

    private String requireUserId(ToolContext toolContext) {
        Object userIdValue = toolContext.getContext().get("userId");
        if (!(userIdValue instanceof String userId) || userId.isBlank()) {
            throw new IllegalStateException("AI 工具上下文缺少 userId");
        }
        return userId;
    }

    private String requireLandId(ToolContext toolContext) {
        Object landIdValue = toolContext.getContext().get("landId");
        if (!(landIdValue instanceof String landId) || landId.isBlank()) {
            throw new IllegalStateException("AI 工具上下文缺少 landId");
        }
        return landId;
    }

    private ReferenceCollector requireCollector(ToolContext toolContext) {
        Object collectorValue = toolContext.getContext().get("referenceCollector");
        if (!(collectorValue instanceof ReferenceCollector collector)) {
            throw new IllegalStateException("AI 工具上下文缺少引用收集器");
        }
        return collector;
    }
}

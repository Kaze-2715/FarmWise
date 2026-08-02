package com.farmwise.irrigation.service;

import static com.farmwise.common.util.ValidationUtil.validateFilter;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmwise.common.exception.BizException;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.model.Device;
import com.farmwise.device.mqtt.MqttClientManager;
import com.farmwise.irrigation.dto.IrrigationBatchResponse;
import com.farmwise.irrigation.dto.IrrigationConfigControllerRow;
import com.farmwise.irrigation.dto.IrrigationConfigResponse;
import com.farmwise.irrigation.dto.IrrigationConfigRow;
import com.farmwise.irrigation.dto.IrrigationRecordResponse;
import com.farmwise.irrigation.dto.SaveIrrigationConfigRequest;
import com.farmwise.irrigation.dto.StartIrrigationRequest;
import com.farmwise.irrigation.mapper.IrrigationMapper;
import com.farmwise.irrigation.model.IrrigationConfig;
import com.farmwise.irrigation.model.IrrigationRecord;
import com.farmwise.irrigation.mqtt.IrrigationCommand;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.land.model.Land;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrrigationService {
    private static final Set<String> IRRIGATION_RECORD_STATUSES =
            Set.of("pending", "running", "completed", "failed");

    private final IrrigationMapper irrigationMapper;
    private final LandMapper landMapper;
    private final DeviceMapper deviceMapper;
    private final MqttClientManager mqttClientManager;
    private final TransactionTemplate transactionTemplate;

    @Transactional(readOnly = true)
    public List<IrrigationRecordResponse> listIrrigationRecords(
            String userId,
            String landId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String status) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        status = validateFilter(status, IRRIGATION_RECORD_STATUSES, "不支持的灌溉记录状态");

        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        if (startAt != null && endAt != null && startAt.isAfter(endAt)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "起始时间不能晚于结束时间");
        }

        return irrigationMapper.findRecordsByConditions(landId, startAt, endAt, status)
                .stream()
                .map(IrrigationRecordResponse::from)
                .toList();
    }

    @Transactional
    public void enableConfig(String userId, String landId, String configId) {
        validateLandAndConfig(userId, landId, configId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        irrigationMapper.disableConfigsByLandId(landId, userId, now);

        int affectedRows = irrigationMapper.enableConfig(landId, configId, userId, now);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "启用灌溉配置失败");
        }
    }

    @Transactional
    public IrrigationConfigResponse updateConfig(
            String userId, String landId, String configId, SaveIrrigationConfigRequest request) {
        if (request.triggerMoisture().compareTo(request.targetMoisture()) >= 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "触发灌溉湿度应当小于目标湿度");
        }

        List<String> deviceIds = request.controllerDeviceIds().stream().distinct().toList();

        validateLandAndConfig(userId, landId, configId);

        validateControllers(userId, landId, deviceIds);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        IrrigationConfig config = new IrrigationConfig(
                configId, // 保留原 ID
                landId,
                request.name().strip(),
                deviceIds,
                request.mode(),
                request.enabled(),
                request.triggerMoisture(),
                request.targetMoisture(),
                request.defaultDuration(),
                userId,
                now);

        if (config.enabled()) {
            irrigationMapper.disableConfigsByLandId(landId, userId, now);
        }

        int affectedRows = irrigationMapper.updateConfig(config);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新配置失败");
        }

        irrigationMapper.deleteConfigControllersByConfigId(configId);

        for (String deviceId : deviceIds) {
            affectedRows = irrigationMapper.addConfigController(configId, deviceId, now);
            if (affectedRows != 1) {
                throw new BizException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "更新配置对应灌溉控制器失败");
            }
        }

        return IrrigationConfigResponse.from(config);
    }

    @Transactional(readOnly = true)
    public List<IrrigationConfigResponse> listConfigs(String userId, String landId) {
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在"));
        List<IrrigationConfigRow> configRows = irrigationMapper.findConfigRowsByLandId(landId);

        List<IrrigationConfigControllerRow> controllerRows =
                irrigationMapper.findConfigControllerRowsByLandId(landId);

        Map<String, List<String>> controllerIdsByConfigId =
                controllerRows.stream().collect(Collectors.groupingBy(
                        IrrigationConfigControllerRow::configId,
                        Collectors.mapping(
                                IrrigationConfigControllerRow::controllerDeviceId,
                                Collectors.toList())));

        return configRows.stream()
                .map(row
                     -> new IrrigationConfig(
                             row.id(),
                             row.landId(),
                             row.name(),
                             controllerIdsByConfigId.getOrDefault(row.id(), List.of()),
                             row.mode(),
                             row.enabled(),
                             row.triggerMoisture(),
                             row.targetMoisture(),
                             row.defaultDuration(),
                             row.updatedBy(),
                             row.updatedAt()))
                .map(IrrigationConfigResponse::from)
                .toList();
    }

    @Transactional
    public void deleteConfig(String userId, String landId, String configId) {
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在"));

        boolean existsConfig = irrigationMapper.existsConfigByIdAndLandId(configId, landId);

        if (!existsConfig) {
            throw new BizException(HttpStatus.NOT_FOUND, "灌溉配置不存在");
        }

        boolean existsActiveRecord = irrigationMapper.existsActiveRecordByLandId(landId);

        if (existsActiveRecord) {
            throw new BizException(HttpStatus.CONFLICT, "存在进行中的灌溉记录");
        }

        int affectedRows = irrigationMapper.deleteConfigByIdAndLandId(configId, landId);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "删除灌溉配置失败");
        }
    }

    @Transactional
    public IrrigationConfigResponse createConfig(
            String userId, String landId, SaveIrrigationConfigRequest request) {
        if (request.triggerMoisture().compareTo(request.targetMoisture()) >= 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "触发湿度应该低于目标湿度");
        }

        List<String> deviceIds = request.controllerDeviceIds().stream().distinct().toList();

        validateControllers(userId, landId, deviceIds);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        IrrigationConfig config = new IrrigationConfig(
                UUID.randomUUID().toString(),
                landId,
                request.name().strip(),
                deviceIds,
                request.mode(),
                request.enabled(),
                request.triggerMoisture(),
                request.targetMoisture(),
                request.defaultDuration(),
                userId,
                now);

        if (config.enabled()) {
            irrigationMapper.disableConfigsByLandId(landId, userId, now);
        }

        int affectedRows = irrigationMapper.addConfig(config);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "创建灌溉配置失败");
        }

        for (String deviceId : deviceIds) {
            int controllerRows = irrigationMapper.addConfigController(config.id(), deviceId, now);

            if (controllerRows != 1) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "保存灌溉控制器关联失败");
            }
        }

        return IrrigationConfigResponse.from(config);
    }

    public void stopIrrigation(String userId, String recordId) {
        if (!mqttClientManager.isAvailable()) {
            throw new BizException(HttpStatus.SERVICE_UNAVAILABLE, "MQTT 服务当前不可用");
        }

        IrrigationRecord record =
                irrigationMapper.findByIdAndOwnerId(recordId, userId)
                        .orElseThrow(
                                () -> new BizException(HttpStatus.NOT_FOUND, "当前灌溉记录不存在"));

        if (!"running".equals(record.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有灌溉中的任务可以停止");
        }

        mqttClientManager.publishCommand(
                record.controllerDeviceId(),
                new IrrigationCommand(record.id(), "stop", null, Instant.now()));
    }

    public IrrigationBatchResponse startIrrigation(String userId, StartIrrigationRequest request) {
        if (!mqttClientManager.isAvailable()) {
            throw new BizException(HttpStatus.SERVICE_UNAVAILABLE, "MQTT 服务当前不可用");
        }
        Land land =
                landMapper.findByIdAndOwnerId(request.landId(), userId)
                        .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在"));

        List<String> controllerIds =
                request.controllerDeviceIds().stream().map(String::strip).distinct().toList();

        if (controllerIds.size() != request.controllerDeviceIds().size()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "控制器 ID 不能重复");
        }

        List<Device> devices = validateControllers(userId, land.id(), controllerIds);

        devices.forEach(device -> {
            if (!"online".equals(device.status())) {
                throw new BizException(
                        HttpStatus.BAD_REQUEST, "尝试调用离线设备: deviceId=" + device.id());
            }
        });

        String batchId = UUID.randomUUID().toString();

        List<IrrigationRecord> records =
                createRecords(userId, land.id(), batchId, controllerIds, request.plannedDuration());

        transactionTemplate.executeWithoutResult(status -> {
            int affectedRows = irrigationMapper.addIrrigationRecord(records);

            if (affectedRows != records.size()) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "创建灌溉记录失败");
            }
        });

        for (IrrigationRecord record : records) {
            try {
                mqttClientManager.publishCommand(
                        record.controllerDeviceId(),
                        new IrrigationCommand(
                                record.id(), "start", request.plannedDuration(), Instant.now()));
            } catch (IllegalStateException exception) {
                irrigationMapper.markFailed(record.id(), LocalDateTime.now(ZoneOffset.UTC));

                log.error(
                        "灌溉任务发布失败，recordId={}, reason={}",
                        record.id(),
                        exception.getMessage(),
                        exception);
            }
        }

        List<IrrigationRecordResponse> responses =
                records.stream().map(IrrigationRecordResponse::from).toList();

        return new IrrigationBatchResponse(batchId, land.id(), responses);
    }

    private List<IrrigationRecord> createRecords(
            String userId,
            String landId,
            String batchId,
            List<String> controllerIds,
            int plannedDuration) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        return controllerIds.stream()
                .map(deviceId
                     -> new IrrigationRecord(
                             UUID.randomUUID().toString(),
                             batchId,
                             landId,
                             deviceId,
                             "manual",
                             "pending",
                             null,
                             null,
                             plannedDuration,
                             0,
                             null,
                             "manually started",
                             userId,
                             now,
                             now))
                .toList();
    }

    private List<Device> validateControllers(
            String userId, String landId, List<String> controllerIds) {
        List<Device> devices = transactionTemplate.execute(
                status
                -> controllerIds.stream()
                           .map(deviceId
                                -> deviceMapper.findByIdAndOwnerId(deviceId, userId)
                                           .orElseThrow(
                                                   ()
                                                           -> new BizException(
                                                                   HttpStatus.NOT_FOUND,
                                                                   "设备不存在, deviceId="
                                                                           + deviceId)))
                           .toList());

        for (Device device : devices) {
            if (!landId.equals(device.landId())) {
                throw new BizException(HttpStatus.NOT_FOUND, "设备不存在");
            }

            if (!"irrigation_controller".equals(device.deviceType())) {
                throw new BizException(
                        HttpStatus.BAD_REQUEST,
                        "设备类型错误，灌溉控制只支持类型为 irrigation controller 的设备, "
                        + "当前设备类型为: "
                                + device.deviceType());
            }
        }

        return devices;
    }

    private void validateLandAndConfig(String userId, String landId, String configId) {
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在"));

        boolean existsConfig = irrigationMapper.existsConfigByIdAndLandId(configId, landId);

        if (!existsConfig) {
            throw new BizException(HttpStatus.NOT_FOUND, "灌溉配置不存在");
        }
    }
}

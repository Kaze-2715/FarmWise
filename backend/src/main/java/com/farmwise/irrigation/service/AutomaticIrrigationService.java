package com.farmwise.irrigation.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmwise.device.event.SoilMoistureReportedEvent;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.model.Device;
import com.farmwise.device.mqtt.MqttClientManager;
import com.farmwise.irrigation.dto.IrrigationConfigRow;
import com.farmwise.irrigation.mapper.IrrigationMapper;
import com.farmwise.irrigation.model.IrrigationRecord;
import com.farmwise.irrigation.mqtt.IrrigationCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomaticIrrigationService {
    private final IrrigationMapper irrigationMapper;
    private final DeviceMapper deviceMapper;

    private final MqttClientManager mqttClientManager;

    private final TransactionTemplate transactionTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSoilMoistureReported(SoilMoistureReportedEvent event) {
        try {
            IrrigationConfigRow config =
                    irrigationMapper.findEnabledAutomaticConfig(event.landId()).orElse(null);

            if (config == null) {
                return;
            }

            if (event.moisture().compareTo(config.triggerMoisture()) <= 0) {
                startIfIdle(config, event);
                return;
            }

            if (event.moisture().compareTo(config.targetMoisture()) >= 0) {
                stopIfRunning(config, event);
            }
        } catch (RuntimeException exception) {
            log.error("自动灌溉处理失败", exception);
        }
    }

    private void startIfIdle(IrrigationConfigRow config, SoilMoistureReportedEvent event) {
        List<IrrigationRecord> records =
                transactionTemplate.execute(status -> prepareAutomaticStart(config, event));

        if (records == null || records.isEmpty()) {
            return;
        }

        publishStartCommands(records);
    }

    private void stopIfRunning(IrrigationConfigRow config, SoilMoistureReportedEvent event) {
        List<IrrigationRecord> records =
                irrigationMapper.findActiveAutomaticRecordsByLandId(config.landId());

        if (records.isEmpty()) {
            return;
        }

        Instant issuedAt = Instant.now();

        for (IrrigationRecord record : records) {
            try {
                mqttClientManager.publishCommand(
                        record.controllerDeviceId(),
                        new IrrigationCommand(record.id(), "stop", null, issuedAt));

                log.info(
                        "自动灌溉停止命令已发布，recordId={}, moisture={}, target={}",
                        record.id(),
                        event.moisture(),
                        config.targetMoisture());
            } catch (IllegalStateException exception) {
                log.error(
                        "自动灌溉停止命令发布失败，recordId={}, reason={}",
                        record.id(),
                        exception.getMessage(),
                        exception);
            }
        }
    }

    private void publishStartCommands(List<IrrigationRecord> records) {
        Instant issuedAt = Instant.now();

        for (IrrigationRecord record : records) {
            try {
                mqttClientManager.publishCommand(
                        record.controllerDeviceId(),
                        new IrrigationCommand(
                                record.id(), "start", record.plannedDuration(), issuedAt));

                log.info(
                        "自动灌溉命令已发布, recordId={}, deviceId={}",
                        record.id(),
                        record.controllerDeviceId());
            } catch (IllegalStateException exception) {
                irrigationMapper.markFailed(record.id(), LocalDateTime.now(ZoneOffset.UTC));
                log.error(
                        "自动灌溉启动命令发布失败，recordId={}, reason={}",
                        record.id(),
                        exception.getMessage(),
                        exception);
            }
        }
    }

    private List<IrrigationRecord> prepareAutomaticStart(
            IrrigationConfigRow config, SoilMoistureReportedEvent event) {
        IrrigationConfigRow lockedConfig =
                irrigationMapper.findEnabledAutomaticConfigForUpdate(config.id(), config.landId())
                        .orElse(null);

        if (lockedConfig == null) {
            return List.of();
        }

        if (event.moisture().compareTo(lockedConfig.triggerMoisture()) > 0) {
            return List.of();
        }

        if (irrigationMapper.existsActiveRecordByLandId(lockedConfig.landId())) {
            return List.of();
        }

        List<Device> controllers = deviceMapper.findByIrrigationConfigId(lockedConfig.id());

        if (controllers.isEmpty()
            || controllers.stream().anyMatch(device -> !"online".equals(device.status()))) {
            return List.of();
        }

        List<IrrigationRecord> records = createAutomaticRecords(lockedConfig, event, controllers);

        int affectedRows = irrigationMapper.addIrrigationRecord(records);

        if (affectedRows != records.size()) {
            throw new IllegalStateException("创建自动灌溉记录失败");
        }

        return records;
    }

    private List<IrrigationRecord> createAutomaticRecords(
            IrrigationConfigRow config, SoilMoistureReportedEvent event, List<Device> controllers) {
        String batchId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        String triggerReason =
                "soil moisture %s%% reached trigger %s%%, sensorId=%s, messageId=%s"
                        .formatted(
                                event.moisture(),
                                config.triggerMoisture(),
                                event.deviceId(),
                                event.messageId())
                        .strip();

        return controllers.stream()
                .map(controller
                     -> new IrrigationRecord(
                             UUID.randomUUID().toString(),
                             batchId,
                             config.landId(),
                             controller.id(),
                             "automatic",
                             "pending",
                             null,
                             null,
                             config.defaultDuration(),
                             0,
                             null,
                             triggerReason,
                             null,
                             now,
                             now))
                .toList();
    }
}

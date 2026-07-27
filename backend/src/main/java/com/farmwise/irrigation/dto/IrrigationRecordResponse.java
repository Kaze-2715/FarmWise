package com.farmwise.irrigation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.farmwise.irrigation.model.IrrigationRecord;

public record IrrigationRecordResponse(
        String id,
        String batchId,
        String landId,
        String controllerDeviceId,
        String source,
        String status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        int plannedDuration,
        int duration,
        BigDecimal waterUsage,
        String triggerReason,
        String operatorId) {
    public static IrrigationRecordResponse from(IrrigationRecord record) {
        return new IrrigationRecordResponse(
                record.id(),
                record.batchId(),
                record.landId(),
                record.controllerDeviceId(),
                record.source(),
                record.status(),
                record.startedAt(),
                record.endedAt(),
                record.plannedDuration(),
                record.duration(),
                record.waterUsage(),
                record.triggerReason(),
                record.operatorId());
    }
}

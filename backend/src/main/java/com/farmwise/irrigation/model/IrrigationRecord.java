package com.farmwise.irrigation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IrrigationRecord(
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
        String operatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}

package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IrrigationRecordData(
        String id,
        String controllerDeviceId,
        String source,
        String status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        int plannedDuration,
        int duration,
        BigDecimal waterUsage,
        String triggerReason) {
}

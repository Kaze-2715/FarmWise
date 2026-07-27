package com.farmwise.irrigation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IrrigationConfigRow(
        String id,
        String landId,
        String name,
        String mode,
        boolean enabled,
        BigDecimal triggerMoisture,
        BigDecimal targetMoisture,
        int defaultDuration,
        String updatedBy,
        LocalDateTime updatedAt) {}

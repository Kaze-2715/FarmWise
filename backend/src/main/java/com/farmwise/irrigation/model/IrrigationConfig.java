package com.farmwise.irrigation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record IrrigationConfig(
        String id,
        String landId,
        String name,
        List<String> controllerDeviceIds,
        String mode,
        boolean enabled,
        BigDecimal triggerMoisture,
        BigDecimal targetMoisture,
        int defaultDuration,
        String updatedBy,
        LocalDateTime updatedAt) {}

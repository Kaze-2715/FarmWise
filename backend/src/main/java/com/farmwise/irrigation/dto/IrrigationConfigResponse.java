package com.farmwise.irrigation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.farmwise.irrigation.model.IrrigationConfig;

public record IrrigationConfigResponse(
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
        LocalDateTime updatedAt) {
    public static IrrigationConfigResponse from(IrrigationConfig config) {
        return new IrrigationConfigResponse(
                config.id(),
                config.landId(),
                config.name(),
                config.controllerDeviceIds(),
                config.mode(),
                config.enabled(),
                config.triggerMoisture(),
                config.targetMoisture(),
                config.defaultDuration(),
                config.updatedBy(),
                config.updatedAt());
    }
}

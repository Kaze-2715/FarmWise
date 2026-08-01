package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record IrrigationConfigData(
        String id,
        String name,
        String mode,
        boolean enabled,
        BigDecimal triggerMoisture,
        BigDecimal targetMoisture,
        int defaultDuration,
        List<String> controllerDeviceIds,
        LocalDateTime updatedAt) {

    public IrrigationConfigData {
        controllerDeviceIds = List.copyOf(controllerDeviceIds);
    }
}

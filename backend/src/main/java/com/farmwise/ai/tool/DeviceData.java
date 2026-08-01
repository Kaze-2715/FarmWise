package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeviceData(
        String id,
        String name,
        String deviceType,
        String status,
        BigDecimal battery,
        LocalDateTime lastReportedAt,
        String model) {
}

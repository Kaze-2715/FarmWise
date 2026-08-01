package com.farmwise.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LatestSensorReadingRow(
        String deviceId,
        String deviceName,
        String landId,
        LocalDateTime recordedAt,
        String metric,
        String unit,
        BigDecimal value) {
}

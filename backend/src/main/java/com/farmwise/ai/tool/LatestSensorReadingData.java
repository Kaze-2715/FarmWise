package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LatestSensorReadingData(
        String deviceId,
        String deviceName,
        String metric,
        String label,
        BigDecimal value,
        String unit,
        LocalDateTime recordedAt) {
}

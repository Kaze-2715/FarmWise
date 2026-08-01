package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnvironmentThresholdData(
        String metric,
        String label,
        BigDecimal minValue,
        BigDecimal maxValue,
        String unit,
        LocalDateTime updatedAt) {
}

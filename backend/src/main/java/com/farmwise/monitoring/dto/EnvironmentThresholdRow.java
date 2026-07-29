package com.farmwise.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnvironmentThresholdRow(
        String landId,
        String metric,
        BigDecimal minValue,
        BigDecimal maxValue,
        boolean enabled,
        String creator,
        LocalDateTime updatedAt) {
}
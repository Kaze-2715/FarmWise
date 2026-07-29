package com.farmwise.monitoring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnvironmentThreshold(
        String landId,
        String metric,
        BigDecimal minValue,
        BigDecimal maxValue,
        boolean enabled,
        String creatorId,
        LocalDateTime updatedAt) {
}

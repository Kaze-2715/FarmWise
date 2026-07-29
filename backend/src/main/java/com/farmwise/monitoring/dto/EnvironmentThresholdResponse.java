package com.farmwise.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnvironmentThresholdResponse(
        String landId,
        String metric,
        BigDecimal min,
        BigDecimal max,
        boolean enabled,
        String creator,
        LocalDateTime updatedAt) {
    public static EnvironmentThresholdResponse from(
            EnvironmentThresholdRow row) {
        return new EnvironmentThresholdResponse(
                row.landId(),
                row.metric(),
                row.minValue(),
                row.maxValue(),
                row.enabled(),
                row.creator(),
                row.updatedAt());
    }
}

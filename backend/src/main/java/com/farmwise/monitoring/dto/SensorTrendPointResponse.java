package com.farmwise.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SensorTrendPointResponse(
        String metric,
        String unit,
        LocalDateTime bucketStart,
        BigDecimal averageValue,
        Long sampleCount,
        Long deviceCount) {
}

package com.farmwise.alert.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertQueryRow(
        String id,
        String landId,
        String type,
        String severity,
        String title,
        String description,
        String suggestion,
        String status,
        LocalDateTime occurredAt,
        String sourceDeviceId,
        String sourceMetric,
        BigDecimal sourceValue,
        String sourceUnit,
        String handleMeasure,
        LocalDateTime handledAt,
        String handleResult,
        String handleRemark,
        String handleOperator) {
}

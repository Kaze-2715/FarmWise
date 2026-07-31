package com.farmwise.alert.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Alert(
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
        String handleOperatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

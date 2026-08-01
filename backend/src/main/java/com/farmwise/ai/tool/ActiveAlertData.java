package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ActiveAlertData(
        String id,
        String type,
        String severity,
        String title,
        String description,
        String suggestion,
        String status,
        LocalDateTime occurredAt,
        String sourceMetric,
        BigDecimal sourceValue,
        String sourceUnit) {
}

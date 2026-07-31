package com.farmwise.alert.dto;

import java.math.BigDecimal;

public record AlertSourceResponse(
        String deviceId,
        String metric,
        BigDecimal value,
        String unit) {
}

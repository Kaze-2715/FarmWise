package com.farmwise.alert.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

public record AlertSourceRequest(
        String deviceId,

        String metric,

        @Digits(integer = 12, fraction = 4, message = "预警来源值最多允许 12 位整数和 4 位小数")
        BigDecimal value,

        @Size(max = 20, message = "预警来源单位不能超过 20 个字符")
        String unit) {
}

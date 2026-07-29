package com.farmwise.monitoring.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record UpdateEnvironmentThresholdRequest(
        @NotNull(message = "阈值下限不能为空")
        @Digits(integer = 12, fraction = 4, message = "阈值下限最多保留四位小数")
        BigDecimal min,

        @NotNull(message = "阈值上限不能为空")
        @Digits(integer = 12, fraction = 4, message = "阈值上限最多保留四位小数")
        BigDecimal max,

        @NotNull(message = "是否启用阈值不能为空")
        Boolean enabled) {
}

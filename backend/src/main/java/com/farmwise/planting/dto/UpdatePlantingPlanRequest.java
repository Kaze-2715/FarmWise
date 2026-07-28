package com.farmwise.planting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePlantingPlanRequest(
        @NotBlank(message = "所属地块不能为空")
        String landId,

        @NotBlank(message = "计划名称不能为空")
        @Size(max = 100, message = "计划名称不应超过 100 个字符")
        String planName,

        @NotBlank(message = "作物类型不能为空")
        @Size(max = 100, message = "作物类型不应超过 100 个字符")
        String cropType,

        @NotNull(message = "计划面积不能为空")
        @DecimalMin(value = "0", inclusive = false, message = "计划面积必须大于 0")
        @Digits(integer = 8, fraction = 2, message = "计划面积最多保留两位小数")
        BigDecimal area,

        @NotNull(message = "种植日期不能为空")
        LocalDate plantingDate,

        @NotNull(message = "预计收获日期不能为空")
        LocalDate expectedHarvestDate,

        @Size(max = 500, message = "备注不应超过 500 个字符")
        String remark) {
}

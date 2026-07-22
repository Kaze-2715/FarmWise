package com.farmwise.land.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLandRequest(
        @NotBlank(message = "地块名称不能为空")
        @Size(max = 100, message = "地块名称不应超过 100 个字符")
        String name,
        @NotBlank(message = "土壤类型不能为空") String landType,
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        @Digits(integer = 8, fraction = 2)
        BigDecimal area,
        @Size(max = 100, message = "作物类型不应超过 100 个字符")
        String crop,
        @NotBlank(message = "地块状态不能为空") String status,
        @NotBlank(message = "地块位置不能为空")
        @Size(max = 255, message = "地块位置不应该超过 255 个字符")
        String location,
        @NotNull(message = "地块经度不应该为空")
        @DecimalMin(value = "-180", message = "经度范围应该在 -180 和 180 之间")
        @DecimalMax(value = "180", message = "经度范围应该在 -180 和 180 之间")
        @Digits(integer = 3, fraction = 7)
        BigDecimal longitude,
        @NotNull(message = "地块纬度不应该为空")
        @DecimalMin(value = "-90", message = "纬度范围应该在 -90 和 90 之间")
        @DecimalMax(value = "90", message = "纬度范围应该在 -90 和 90 之间")
        @Digits(integer = 3, fraction = 7)
        BigDecimal latitude) {
}

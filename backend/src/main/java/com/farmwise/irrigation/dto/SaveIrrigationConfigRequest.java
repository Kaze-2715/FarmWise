package com.farmwise.irrigation.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SaveIrrigationConfigRequest(
        @NotBlank(message = "配置名称不能为空")
        @Size(max = 100, message = "配置名称不能超过 100 个字符") String name,

        @NotEmpty(message = "至少绑定一台灌溉控制器")
        List<@NotBlank(message = "控制器 ID 不能为空") String> controllerDeviceIds,

        @NotBlank(message = "灌溉模式不能为空")
        @Pattern(regexp = "manual|automatic", message = "灌溉模式只能是 manual 或 automatic")
        String mode,

        boolean enabled,

        @NotNull(message = "触发湿度不能为空")
        @DecimalMin(value = "0", message = "触发湿度不能小于 0")
        @DecimalMax(value = "100", message = "触发湿度不能大于 100")
        @Digits(integer = 3, fraction = 2, message = "触发湿度最多保留 2 位小数")
        BigDecimal triggerMoisture,

        @NotNull(message = "目标湿度不能为空")
        @DecimalMin(value = "0", message = "目标湿度不能小于 0")
        @DecimalMax(value = "100", message = "目标湿度不能大于 100")
        @Digits(integer = 3, fraction = 2, message = "目标湿度最多保留 2 位小数")
        BigDecimal targetMoisture,

        @Min(value = 1, message = "默认灌溉时长不能少于 1 分钟")
        @Max(value = 180, message = "默认灌溉时长不能超过 180 分钟") int defaultDuration) {}

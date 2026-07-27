package com.farmwise.device.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDeviceRequest(
        @NotBlank(message = "设备名称不能为空")
        @Size(max = 100, message = "设备名称不应超过 100 个字符") String name,

        @NotBlank(message = "设备类型不能为空") String deviceType,

        String landId,

        @NotBlank(message = "设备型号不能为空")
        @Size(max = 100, message = "设备型号不应超过 100 个字符") String model,

        LocalDate installDate,

        @NotNull(message = "设备经度不能为空")
        @DecimalMin(value = "-180", message = "经度范围应在 -180 和 180 之间")
        @DecimalMax(value = "180", message = "经度范围应在 -180 和 180 之间")
        @Digits(integer = 3, fraction = 7) BigDecimal longitude,

        @NotNull(message = "设备纬度不能为空")
        @DecimalMin(value = "-90", message = "纬度范围应在 -90 和 90 之间")
        @DecimalMax(value = "90", message = "纬度范围应在 -90 和 90 之间")
        @Digits(integer = 3, fraction = 7) BigDecimal latitude) {}

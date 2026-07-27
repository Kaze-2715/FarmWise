package com.farmwise.irrigation.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record StartIrrigationRequest(
        @NotBlank(message = "地块不能为空") String landId,

        @NotEmpty(message = "至少选择一台灌溉控制器")
        List<@NotBlank(message = "控制器 ID 不能为空") String> controllerDeviceIds,

        @Min(value = 1, message = "计划灌溉时长不能少于 1 分钟")
        @Max(value = 180, message = "计划灌溉时长不能超过 180 分钟") int plannedDuration) {}

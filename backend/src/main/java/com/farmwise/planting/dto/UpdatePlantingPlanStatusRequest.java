package com.farmwise.planting.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePlantingPlanStatusRequest(
        @NotBlank(message = "目标状态不能为空")
        String status) {
}

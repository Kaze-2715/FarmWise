package com.farmwise.planting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.farmwise.planting.model.PlantingPlan;

public record PlantingPlanResponse(
        String id,
        String landId,
        String planName,
        String cropType,
        BigDecimal area,
        LocalDate plantingDate,
        LocalDate expectedHarvestDate,
        String status,
        String remark) {
    public static PlantingPlanResponse from(PlantingPlan plantingPlan) {
        return new PlantingPlanResponse(
                plantingPlan.id(),
                plantingPlan.landId(),
                plantingPlan.planName(),
                plantingPlan.cropType(),
                plantingPlan.area(),
                plantingPlan.plantingDate(),
                plantingPlan.expectedHarvestDate(),
                plantingPlan.status(),
                plantingPlan.remark());
    }
}

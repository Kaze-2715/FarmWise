package com.farmwise.planting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlantingPlan(
        String id,
        String landId,
        String planName,
        String cropType,
        BigDecimal area,
        LocalDate plantingDate,
        LocalDate expectedHarvestDate,
        String status,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

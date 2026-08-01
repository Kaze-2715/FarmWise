package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlantingPlanData(
        String id,
        String planName,
        String cropType,
        BigDecimal area,
        LocalDate plantingDate,
        LocalDate expectedHarvestDate,
        String status,
        String remark,
        LocalDateTime updatedAt) {
}

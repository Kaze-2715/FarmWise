package com.farmwise.ai.tool;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LandProfileData(
        String name,
        String landType,
        BigDecimal area,
        String crop,
        String status,
        String location,
        LocalDateTime updatedAt) {

}

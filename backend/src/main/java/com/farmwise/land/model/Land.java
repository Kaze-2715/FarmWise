package com.farmwise.land.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Land(
        String id,
        String ownerId,
        String name,
        String landType,
        BigDecimal area,
        String crop,
        String status,
        String location,
        BigDecimal longitude,
        BigDecimal latitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

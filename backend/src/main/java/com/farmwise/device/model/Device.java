package com.farmwise.device.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Device(
        String id,
        String ownerId,
        String landId,
        String name,
        String deviceType,
        String status,
        BigDecimal battery,
        LocalDateTime lastReportedAt,
        String model,
        LocalDate installDate,
        BigDecimal longitude,
        BigDecimal latitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}

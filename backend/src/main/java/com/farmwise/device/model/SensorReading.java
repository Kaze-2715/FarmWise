package com.farmwise.device.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SensorReading(
        String deviceId,
        String landId,
        LocalDateTime recordedAt,
        String metric,
        String unit,
        BigDecimal value) {}

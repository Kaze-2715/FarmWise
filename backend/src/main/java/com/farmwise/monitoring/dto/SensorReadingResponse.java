package com.farmwise.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.farmwise.device.model.SensorReading;

public record SensorReadingResponse(
        String deviceId,
        String landId,
        LocalDateTime recordedAt,
        String metric,
        String unit,
        BigDecimal value) {
    public static SensorReadingResponse from(SensorReading reading) {
        return new SensorReadingResponse(
                reading.deviceId(),
                reading.landId(),
                reading.recordedAt(),
                reading.metric(),
                reading.unit(),
                reading.value());
    }
}

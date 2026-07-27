package com.farmwise.device.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.farmwise.device.model.Device;

public record DeviceResponse(
        String id,
        String name,
        String deviceType,
        String landId,
        String status,
        BigDecimal battery,
        LocalDateTime lastReportedAt,
        String model,
        LocalDate installDate,
        BigDecimal longitude,
        BigDecimal latitude) {
    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
                device.id(),
                device.name(),
                device.deviceType(),
                device.landId(),
                device.status(),
                device.battery(),
                device.lastReportedAt(),
                device.model(),
                device.installDate(),
                device.longitude(),
                device.latitude());
    }
}

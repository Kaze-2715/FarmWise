package com.farmwise.land.dto;

import java.math.BigDecimal;

import com.farmwise.land.model.Land;

public record LandResponse(
        String id,
        String name,
        String landType,
        BigDecimal area,
        String crop,
        String status,
        String location,
        BigDecimal longitude,
        BigDecimal latitude) {
    public static LandResponse from(Land land) {
        return new LandResponse(
                land.id(),
                land.name(),
                land.landType(),
                land.area(),
                land.crop(),
                land.status(),
                land.location(),
                land.longitude(),
                land.latitude());
    }
}

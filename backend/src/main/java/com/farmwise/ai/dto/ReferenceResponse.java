package com.farmwise.ai.dto;

public record ReferenceResponse(
        String type,
        String sourceId,
        String label,
        Object value,
        String unit) {
}

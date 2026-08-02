package com.farmwise.report.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReportSnapshotResponse(
        LandSnapshot land,
        DeviceSnapshot devices,
        List<EnvironmentSnapshot> environment,
        AlertSnapshot alerts,
        TaskSnapshot tasks,
        List<AiAdviceSnapshot> aiAdvice) {

    public record LandSnapshot(
            String id,
            String name,
            String crop,
            BigDecimal area) {
    }

    public record DeviceSnapshot(
            int total,
            int online,
            int offline,
            int lowBattery) {
    }

    public record EnvironmentSnapshot(
            String metric,
            BigDecimal value,
            String unit,
            String status,
            LocalDateTime recordedAt) {
    }

    public record AlertSnapshot(
            int total,
            int pending,
            int processing,
            int resolved,
            int ignored) {
    }

    public record TaskSnapshot(
            int total,
            int pending,
            int processing,
            int completed,
            int cancelled) {
    }

    public record AiAdviceSnapshot(
            String messageId,
            String content,
            LocalDateTime createdAt,
            List<ReferenceSnapshot> references) {
    }

    public record ReferenceSnapshot(
            String type,
            String sourceId,
            String label,
            Object value,
            String unit) {
    }
}

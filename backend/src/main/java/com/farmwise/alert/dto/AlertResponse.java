package com.farmwise.alert.dto;

import java.time.LocalDateTime;

import com.farmwise.alert.model.Alert;

public record AlertResponse(
        String id,
        String landId,
        String type,
        String severity,
        String title,
        String description,
        String suggestion,
        String status,
        LocalDateTime occurredAt,
        AlertSourceResponse source,
        AlertHandleRecordResponse handleRecord) {
    public static AlertResponse from(Alert alert) {
        return from(alert, alert.status());
    }

    public static AlertResponse from(Alert alert, String status) {
        return new AlertResponse(
                alert.id(),
                alert.landId(),
                alert.type(),
                alert.severity(),
                alert.title(),
                alert.description(),
                alert.suggestion(),
                status,
                alert.occurredAt(),
                new AlertSourceResponse(
                        alert.sourceDeviceId(),
                        alert.sourceMetric(),
                        alert.sourceValue(),
                        alert.sourceUnit()),
                null);
    }

    public static AlertResponse from(AlertQueryRow row) {
        AlertHandleRecordResponse handleRecord = row.handledAt() == null
                ? null
                : new AlertHandleRecordResponse(
                        row.handleMeasure(),
                        row.handledAt(),
                        row.handleResult(),
                        row.handleRemark(),
                        row.handleOperator());

        return new AlertResponse(
                row.id(),
                row.landId(),
                row.type(),
                row.severity(),
                row.title(),
                row.description(),
                row.suggestion(),
                row.status(),
                row.occurredAt(),
                new AlertSourceResponse(
                        row.sourceDeviceId(),
                        row.sourceMetric(),
                        row.sourceValue(),
                        row.sourceUnit()),
                handleRecord);
    }
}

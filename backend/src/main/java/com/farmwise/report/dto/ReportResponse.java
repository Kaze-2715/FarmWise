package com.farmwise.report.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.farmwise.report.model.Report;

public record ReportResponse(
        String id,
        String landId,
        String type,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String creatorId,
        LocalDateTime createdAt,
        LocalDateTime generatedAt,
        String summary,
        ReportSnapshotResponse snapshot) {
    public static ReportResponse from(
            Report report,
            ReportSnapshotResponse snapshot) {
        return new ReportResponse(
                report.id(),
                report.landId(),
                report.type(),
                report.title(),
                report.startDate(),
                report.endDate(),
                report.status(),
                report.creatorId(),
                report.createdAt(),
                report.generatedAt(),
                report.summary(),
                snapshot);
    }
}

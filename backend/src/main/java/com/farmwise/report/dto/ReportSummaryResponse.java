package com.farmwise.report.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.farmwise.report.model.ReportSummary;

public record ReportSummaryResponse(
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
        String summary) {
    public static ReportSummaryResponse from(ReportSummary report) {
        return new ReportSummaryResponse(
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
                report.summary());
    }
}

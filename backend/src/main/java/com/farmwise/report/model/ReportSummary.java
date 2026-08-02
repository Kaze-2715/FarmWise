package com.farmwise.report.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportSummary(
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
}

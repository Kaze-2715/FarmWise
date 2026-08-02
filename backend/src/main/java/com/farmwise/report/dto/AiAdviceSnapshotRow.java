package com.farmwise.report.dto;

import java.time.LocalDateTime;

public record AiAdviceSnapshotRow(
        String messageId,
        String content,
        String referencesJson,
        LocalDateTime createdAt) {
}
package com.farmwise.ai.dto;

import java.time.LocalDateTime;

public record TaskDraftResponse(
        String taskType,
        String title,
        String description,
        String priority,
        String assigneeId,
        LocalDateTime deadline) {
}

package com.farmwise.ai.tool;

import java.time.LocalDateTime;

public record ActiveFarmTaskData(
        String id,
        String sourceType,
        String sourceId,
        String taskType,
        String title,
        String description,
        String priority,
        String status,
        String assigneeId,
        LocalDateTime deadline) {
}

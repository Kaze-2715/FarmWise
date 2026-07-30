package com.farmwise.task.model;

import java.time.LocalDateTime;

public record FarmTask(
        String id,
        String landId,
        String sourceType,
        String sourceId,
        String taskType,
        String title,
        String description,
        String priority,
        String status,
        String assigneeId,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        String result,
        String remark,
        LocalDateTime updatedAt) {
}

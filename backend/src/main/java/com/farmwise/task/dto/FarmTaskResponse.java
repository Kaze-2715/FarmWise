package com.farmwise.task.dto;

import java.time.LocalDateTime;

import com.farmwise.task.model.FarmTask;

public record FarmTaskResponse(
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
        String remark) {
    public static FarmTaskResponse from(FarmTask task) {
        return new FarmTaskResponse(
                task.id(),
                task.landId(),
                task.sourceType(),
                task.sourceId(),
                task.taskType(),
                task.title(),
                task.description(),
                task.priority(),
                task.status(),
                task.assigneeId(),
                task.deadline(),
                task.createdAt(),
                task.completedAt(),
                task.result(),
                task.remark());
    }
}

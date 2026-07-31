package com.farmwise.alert.dto;

import java.time.LocalDateTime;

public record StartAlertRequest(
        boolean createTask,
        String taskType,
        String priority,
        String assigneeId,
        LocalDateTime deadline) {
}

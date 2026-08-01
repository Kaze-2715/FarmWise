package com.farmwise.ai.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskFromMessageRequest(
        @NotBlank(message = "负责人不能为空")
        String assigneeId,

        @NotNull(message = "截止时间不能为空")
        LocalDateTime deadline) {
}

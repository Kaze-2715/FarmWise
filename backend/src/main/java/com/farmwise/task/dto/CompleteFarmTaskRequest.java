package com.farmwise.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteFarmTaskRequest(
        @NotBlank(message = "任务完成结果不能为空")
        @Size(max = 1000, message = "任务完成结果不能超过 1000 个字符")
        String result) {
}

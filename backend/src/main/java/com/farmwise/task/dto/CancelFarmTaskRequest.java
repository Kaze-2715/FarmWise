package com.farmwise.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelFarmTaskRequest(
        @NotBlank(message = "取消原因不能为空")
        @Size(max = 1000, message = "取消原因不能超过 1000 个字符")
        String reason) {
}

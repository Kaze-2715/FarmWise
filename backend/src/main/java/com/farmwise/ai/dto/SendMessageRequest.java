package com.farmwise.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "消息内容不能为空")
        String content) {
}

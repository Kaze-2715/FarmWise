package com.farmwise.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
        @NotBlank(message = "所属地块不能为空")
        String landId,

        @NotBlank(message = "对话标题不能为空")
        @Size(max = 150, message = "对话标题不能超过 150 个字符")
        String title) {
}

package com.farmwise.ai.model;

import java.time.LocalDateTime;

public record ConversationMessage(
        String id,
        String conversationId,
        String role,
        String content,
        String referencesJson,
        String taskDraftJson,
        LocalDateTime createdAt) {
}

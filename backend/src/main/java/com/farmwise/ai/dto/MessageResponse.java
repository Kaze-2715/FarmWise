package com.farmwise.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.farmwise.ai.model.ConversationMessage;

public record MessageResponse(
        String id,
        String role,
        String content,
        LocalDateTime createdAt,
        List<ReferenceResponse> references,
        TaskDraftResponse taskDraft) {
    public static MessageResponse from(
            ConversationMessage message,
            List<ReferenceResponse> references,
            TaskDraftResponse taskDraft) {
        return new MessageResponse(
                message.id(),
                message.role(),
                message.content(),
                message.createdAt(),
                List.copyOf(references),
                taskDraft);
    }
}

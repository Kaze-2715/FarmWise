package com.farmwise.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.farmwise.ai.model.Conversation;

public record ConversationResponse(
        String id,
        String landId,
        String title,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MessageResponse> messages) {
    public static ConversationResponse from(
            Conversation conversation,
            List<MessageResponse> messages) {
        return new ConversationResponse(
                conversation.id(),
                conversation.landId(),
                conversation.title(),
                conversation.status(),
                conversation.createdAt(),
                conversation.updatedAt(),
                List.copyOf(messages));
    }
}

package com.farmwise.ai.dto;

import java.time.LocalDateTime;

import com.farmwise.ai.model.Conversation;

public record ConversationSummaryResponse(
        String id,
        String landId,
        String title,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static ConversationSummaryResponse from(Conversation conversation) {
        return new ConversationSummaryResponse(
                conversation.id(),
                conversation.landId(),
                conversation.title(),
                conversation.status(),
                conversation.createdAt(),
                conversation.updatedAt());
    }
}

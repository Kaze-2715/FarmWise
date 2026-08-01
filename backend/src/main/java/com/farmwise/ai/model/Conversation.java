package com.farmwise.ai.model;

import java.time.LocalDateTime;

public record Conversation(
        String id,
        String landId,
        String title,
        String status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

package com.farmwise.ai.model;

public record TaskDraftSuggestion(
        String taskType,
        String title,
        String description,
        String priority) {
}

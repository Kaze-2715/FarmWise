package com.farmwise.ai.dto;

public record SendMessageResponse(
        MessageResponse userMessage,
        MessageResponse assistantMessage) {
}

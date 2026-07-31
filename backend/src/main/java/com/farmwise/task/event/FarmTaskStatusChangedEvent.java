package com.farmwise.task.event;

import java.time.LocalDateTime;

public record FarmTaskStatusChangedEvent(
        String sourceType,
        String sourceId,
        String status,
        LocalDateTime changedAt) {

}

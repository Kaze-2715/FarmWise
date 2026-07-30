package com.farmwise.rbac.model;

import java.time.LocalDateTime;

public record Role(
        String code,
        String name,
        String description,
        LocalDateTime createdAt) {
}

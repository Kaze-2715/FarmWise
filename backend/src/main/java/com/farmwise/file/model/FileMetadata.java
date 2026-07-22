package com.farmwise.file.model;

import java.time.LocalDateTime;

public record FileMetadata(
    String id,
    String ownerId,
    String originalName,
    String contentType,
    long size,
    String purpose,
    String storageProvider,
    String storageKey,
    LocalDateTime createdAt
) {
    
}

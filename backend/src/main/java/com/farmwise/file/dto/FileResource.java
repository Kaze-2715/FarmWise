package com.farmwise.file.dto;

import java.time.LocalDateTime;

import com.farmwise.file.model.FileMetadata;

public record FileResource(
        String id,
        String originalName,
        String contentType,
        long size,
        String url,
        String purpose,
        LocalDateTime createdAt) {

    public static FileResource from(
            FileMetadata metadata,
            String url) {
        return new FileResource(metadata.id(), metadata.originalName(), metadata.contentType(), metadata.size(), url,
                metadata.purpose(), metadata.createdAt());
    }
}

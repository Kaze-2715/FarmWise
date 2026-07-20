package com.farmwise.user.model;

import java.time.LocalDateTime;

public record User(
        String id,
        String username,
        String passwordHash,
        String realName,
        String email,
        boolean emailVerified,
        String phone,
        String avatarFileId,
        String organization,
        String province,
        String city,
        String position,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt) {
    public static User registered(
            String id,
            String username,
            String passwordHash,
            String email,
            LocalDateTime now) {
        return new User(
                id,
                username,
                passwordHash,
                null,
                email,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                "active",
                now,
                now,
                null);
    }

    public User withLastLoginAt(LocalDateTime lastLoginAt) {
        return new User(
                id,
                username,
                passwordHash,
                realName,
                email,
                emailVerified,
                phone,
                avatarFileId,
                organization,
                province,
                city,
                position,
                status,
                createdAt,
                updatedAt,
                lastLoginAt);
    }
}

package com.farmwise.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.farmwise.file.dto.FileUrls;
import com.farmwise.user.model.User;

public record UserProfile(
        String id,
        String username,
        String realName,
        String email,
        boolean emailVerified,
        String phone,
        String avatarUrl,
        String organization,
        String province,
        String city,
        String position,
        String status,
        List<String> roles,
        List<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt) {

    public static UserProfile from(
            User user,
            List<String> roles,
            List<String> permissions
    ) {
        return new UserProfile(
                user.id(),
                user.username(),
                user.realName(),
                user.email(),
                user.emailVerified(),
                user.phone(),
                user.avatarFileId() == null ? null :
                FileUrls.content(user.avatarFileId()),
                user.organization(),
                user.province(),
                user.city(),
                user.position(),
                user.status(),
                roles,
                permissions,
                user.createdAt(),
                user.lastLoginAt());
    }
}

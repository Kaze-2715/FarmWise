package com.farmwise.auth.dto;

import com.farmwise.user.dto.UserProfile;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserProfile user) {
}

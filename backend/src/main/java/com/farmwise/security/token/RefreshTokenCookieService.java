package com.farmwise.security.token;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieService {
    private final RefreshTokenProperties properties;

    public ResponseCookie create(String token) {
        return ResponseCookie
                .from(properties.cookieName(), token)
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(properties.ttl())
                .build();
    }

    public ResponseCookie delete() {
        return ResponseCookie
                .from(properties.cookieName(), "")
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
    }
}

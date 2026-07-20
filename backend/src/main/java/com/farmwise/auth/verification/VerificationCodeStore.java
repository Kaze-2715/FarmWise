package com.farmwise.auth.verification;

import java.time.Duration;
import java.util.Locale;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationCodeStore {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private final StringRedisTemplate redisTemplate;

    public void save(String email, String scene, String code) {
        redisTemplate.opsForValue().set(buildKey(email, scene), code, CODE_TTL);
    }

    public boolean matches(
            String email,
            String scene,
            String submittedCode
    ) {
        String storedCode = redisTemplate.opsForValue().get(buildKey(email, scene));

        return submittedCode.equals(storedCode);
    }

    public void delete(String email, String scene) {
        redisTemplate.delete(buildKey(email, scene));
    }

    private String buildKey(String email, String scene) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        return "auth:verification-code:%s:%s".formatted(scene, normalizedEmail);
    }
}

package com.farmwise.security.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private static final String KEY_PREFIX = "auth:refresh-token:";

    private final StringRedisTemplate redisTemplate;
    private final RefreshTokenProperties properties;

    public void save(String token, String userId) {
        redisTemplate.opsForValue().set(buildKey(token), userId, properties.ttl());
    }

    public Optional<String> findUserId(String token) {
        String userId = redisTemplate.opsForValue().get(buildKey(token));

        return Optional.ofNullable(userId);
    }

    public void delete(String token) {
        redisTemplate.delete(buildKey(token));
    }

    public Optional<String> consume(String token) {
        String userId = redisTemplate.opsForValue().getAndDelete(buildKey(token));

        return Optional.ofNullable(userId);
    }

    private String buildKey(String token) {
        try {
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return KEY_PREFIX + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前 Java 环境不支持 SHA-256", exception);
        }
    }
}

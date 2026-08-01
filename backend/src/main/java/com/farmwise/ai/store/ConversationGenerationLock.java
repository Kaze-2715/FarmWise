package com.farmwise.ai.store;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConversationGenerationLock {
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>(
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1])
            end
            return 0
            """,
            Long.class);
    private static final String KEY_PREFIX = "ai:conversation:generation:";

    private static final Duration LOCK_TTL = Duration.ofMinutes(2);

    private final StringRedisTemplate redisTemplate;

    public Optional<String> tryAcquire(String conversationId) {
        String token = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(keyOf(conversationId), token, LOCK_TTL);

        if (!Boolean.TRUE.equals(acquired)) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    public void release(String conversationId, String token) {
        redisTemplate.execute(RELEASE_SCRIPT, List.of(keyOf(conversationId)), token);
    }

    private String keyOf(String conversationId) {
        return KEY_PREFIX + conversationId;
    }
}

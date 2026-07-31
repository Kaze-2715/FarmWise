package com.farmwise.alert.store;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.farmwise.alert.model.AlertBlockingState;
import com.farmwise.alert.model.AlertDetectionState;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlertStateStore {
    private static final String DETECTION_KEY_PREFIX = "alert:detection:";
    private static final String BLOCKING_KEY_PREFIX = "alert:blocking:";
    private static final String ABNORMAL_KEY_PREFIX = "alert:abnormals:";
    private static final Duration STATE_TTL = Duration.ofHours(24);
    private static final String ABNORMAL_COUNT = "abnormalCount";
    private static final String NORMAL_COUNT = "normalCount";
    private static final String BLOCKING = "blocking";
    private static final String ALERT_ID = "alertId";

    private final StringRedisTemplate redisTemplate;

    public Optional<AlertDetectionState> findDetection(
        String deviceId,
        String metric
    ) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        Map<String, String> entries = hashOps.entries(detectionKeyOf(deviceId, metric));

        if (entries.isEmpty()) {
            return Optional.empty();
        }

        int abnormalCount = Integer.parseInt(entries.get(ABNORMAL_COUNT));
        int normalCount = Integer.parseInt(entries.get(NORMAL_COUNT));

        return Optional.of(new AlertDetectionState(abnormalCount, normalCount));
    }

    public void saveDetection(String deviceId, String metric, AlertDetectionState state) {
        String abnormalCountString = Integer.toString(state.abnormalCount());
        String normalCountString = Integer.toString(state.normalCount());
        Map<String, String> entries = Map.of(
                ABNORMAL_COUNT, abnormalCountString,
                NORMAL_COUNT, normalCountString);
        String key = detectionKeyOf(deviceId, metric);
        redisTemplate.opsForHash().putAll(key, entries);
        redisTemplate.expire(key, STATE_TTL);
    }

    public void deleteDetection(String deviceId, String metric) {
        redisTemplate.delete(detectionKeyOf(deviceId, metric));
    }

    public Optional<AlertBlockingState> findBlocking(String landId, String metric) {
        String key = blockingKeyOf(landId, metric);
        Map<String, String> entries = redisTemplate.<String, String>opsForHash().entries(key);

        if (entries.isEmpty()) {
            return Optional.empty();
        }

        boolean blocking = Boolean.parseBoolean(entries.get(BLOCKING));
        String alertIdValue = entries.get(ALERT_ID);
        String alertId = alertIdValue == null || alertIdValue.isBlank() ? null : alertIdValue;

        redisTemplate.expire(key, STATE_TTL);
        return Optional.of(new AlertBlockingState(blocking, alertId));
    }

    public void saveBlocking(String landId, String metric, AlertBlockingState state) {
        String key = blockingKeyOf(landId, metric);
        String alertId = state.alertId() == null ? "" : state.alertId();

        redisTemplate.opsForHash().putAll(
                key,
                Map.of(
                        BLOCKING, Boolean.toString(state.blocking()),
                        ALERT_ID, alertId));
        redisTemplate.expire(key, STATE_TTL);
    }

    public void addAbnormal(String landId, String metric, String deviceId) {
        String key = abnormalKeyOf(landId, metric);
        redisTemplate.opsForSet().add(key, deviceId);
        redisTemplate.expire(key, STATE_TTL);
    }

    public void removeAbnormal(String landId, String metric, String deviceId) {
        redisTemplate.opsForSet().remove(abnormalKeyOf(landId, metric), deviceId);
    }

    public boolean isAbnormal(String landId, String metric, String deviceId) {
        String key = abnormalKeyOf(landId, metric);
        Boolean member = redisTemplate.opsForSet().isMember(key, deviceId);

        if (Boolean.TRUE.equals(member)) {
            redisTemplate.expire(key, STATE_TTL);
            return true;
        }

        return false;
    }

    public long countAbnormals(String landId, String metric) {
        Long size = redisTemplate.opsForSet().size(abnormalKeyOf(landId, metric));
        return size == null ? 0 : size;
    }

    private String detectionKeyOf(
            String deviceId,
            String metric) {
        return DETECTION_KEY_PREFIX + deviceId + ":" + metric;
    }

    private String blockingKeyOf(String landId, String metric) {
        return BLOCKING_KEY_PREFIX + landId + ":" + metric;
    }

    private String abnormalKeyOf(String landId, String metric) {
        return ABNORMAL_KEY_PREFIX + landId + ":" + metric;
    }
}

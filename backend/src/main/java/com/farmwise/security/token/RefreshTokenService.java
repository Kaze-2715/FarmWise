package com.farmwise.security.token;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.farmwise.common.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenStore refreshTokenStore;

    public String issue(String userId) {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        String token = Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        refreshTokenStore.save(token, userId);

        return token;
    }

    public String consume(String token) {
        return refreshTokenStore
                .consume(token)
                .orElseThrow(() -> new BizException(
                        HttpStatus.UNAUTHORIZED,
                        "刷新令牌无效或已过期"));
    }

    public void revoke(String token) {
        refreshTokenStore.delete(token);
    }
}

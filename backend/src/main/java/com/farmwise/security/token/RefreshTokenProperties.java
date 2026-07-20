package com.farmwise.security.token;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "farmwise.security.refresh-token")
public record RefreshTokenProperties(
        Duration ttl,
        String cookieName,
        boolean secure) {

}

package com.farmwise.security.jwt;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "farmwise.security.jwt")
public record JwtProperties(
        String issuer,
        Duration accessTokenTtl,
        String secret
) {
}

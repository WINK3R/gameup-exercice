package com.gamesUP.gamesUP.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, long expirationSeconds) {

    public JwtProperties {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("security.jwt.secret must be configured");
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException("security.jwt.secret must contain at least 32 characters");
        }
        expirationSeconds = expirationSeconds <= 0 ? 3600 : expirationSeconds;
    }
}

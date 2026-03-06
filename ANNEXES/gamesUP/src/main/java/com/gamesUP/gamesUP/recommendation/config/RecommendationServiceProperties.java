package com.gamesUP.gamesUP.recommendation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recommendation.service")
public record RecommendationServiceProperties(String baseUrl) {

    public RecommendationServiceProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("recommendation.service.base-url must be configured");
        }
    }
}

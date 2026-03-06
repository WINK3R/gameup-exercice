package com.gamesUP.gamesUP.recommendation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(RecommendationServiceProperties.class)
public class RecommendationConfiguration {

    @Bean
    public RestTemplate recommendationRestTemplate() {
        return new RestTemplate();
    }
}

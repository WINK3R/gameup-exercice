package com.gamesUP.gamesUP.recommendation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gamesUP.gamesUP.recommendation.config.RecommendationServiceProperties;
import com.gamesUP.gamesUP.recommendation.dto.RecommendationPayload;
import com.gamesUP.gamesUP.recommendation.dto.RecommendationResponse;
import com.gamesUP.gamesUP.recommendation.exception.RecommendationServiceException;

@Service
public class RecommendationClient {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationClient.class);

    private final RestTemplate restTemplate;
    private final String recommendationsUrl;

    public RecommendationClient(RestTemplate restTemplate, RecommendationServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.recommendationsUrl = UriComponentsBuilder.fromHttpUrl(properties.baseUrl())
                .path("/recommendations/")
                .toUriString();
    }

    public RecommendationResponse fetchRecommendations(RecommendationPayload payload) {
        try {
            ResponseEntity<RecommendationResponse> entity = restTemplate.postForEntity(
                    recommendationsUrl,
                    payload,
                    RecommendationResponse.class
            );
            RecommendationResponse body = entity.getBody();
            if (body == null) {
                throw new RecommendationServiceException("Recommendation API returned an empty body");
            }
            return body;
        } catch (RestClientResponseException ex) {
            String message = "Recommendation API returned status %d: %s".formatted(
                    ex.getStatusCode().value(),
                    ex.getResponseBodyAsString()
            );
            LOG.warn(message);
            throw new RecommendationServiceException(message, ex);
        } catch (RestClientException ex) {
            String message = "Unable to reach recommendation API";
            LOG.error(message, ex);
            throw new RecommendationServiceException(message, ex);
        }
    }
}

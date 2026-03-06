package com.gamesUP.gamesUP.recommendation.exception;

public class RecommendationServiceException extends RuntimeException {
    public RecommendationServiceException(String message) {
        super(message);
    }

    public RecommendationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

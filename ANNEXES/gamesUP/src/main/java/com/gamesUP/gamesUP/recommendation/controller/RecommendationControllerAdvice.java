package com.gamesUP.gamesUP.recommendation.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gamesUP.gamesUP.controller.UserController;
import com.gamesUP.gamesUP.recommendation.exception.RecommendationServiceException;

@RestControllerAdvice(basePackageClasses = UserController.class)
public class RecommendationControllerAdvice {

    @ExceptionHandler(RecommendationServiceException.class)
    public ResponseEntity<Map<String, String>> handleRecommendationError(RecommendationServiceException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", exception.getMessage(),
                        "details", exception.getCause() != null ? exception.getCause().getMessage() : ""
                ));
    }
}

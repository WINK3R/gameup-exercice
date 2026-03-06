package com.gamesUP.gamesUP.recommendation.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationPayload(
        @JsonProperty("user_id") Long userId,
        List<UserPurchaseDto> purchases
) {
    public RecommendationPayload {
        purchases = purchases == null ? List.of() : List.copyOf(purchases);
    }
}

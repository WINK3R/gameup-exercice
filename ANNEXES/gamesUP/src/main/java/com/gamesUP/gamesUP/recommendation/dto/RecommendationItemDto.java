package com.gamesUP.gamesUP.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationItemDto(
        @JsonProperty("game_id") Long gameId,
        @JsonProperty("game_name") String gameName,
        Double score
) {
}

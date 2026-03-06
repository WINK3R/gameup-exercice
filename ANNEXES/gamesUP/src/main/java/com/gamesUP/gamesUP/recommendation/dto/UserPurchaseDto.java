package com.gamesUP.gamesUP.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPurchaseDto(
        @JsonProperty("game_id") Long gameId,
        Double rating
) {
}

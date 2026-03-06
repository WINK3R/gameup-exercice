package com.gamesUP.gamesUP.recommendation.dto;

import java.util.List;

public record RecommendationResponse(List<RecommendationItemDto> recommendations) {
}

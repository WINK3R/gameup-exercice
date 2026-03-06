package com.gamesUP.gamesUP.recommendation.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.recommendation.dto.RecommendationPayload;
import com.gamesUP.gamesUP.recommendation.dto.RecommendationResponse;
import com.gamesUP.gamesUP.recommendation.dto.UserPurchaseDto;
import com.gamesUP.gamesUP.recommendation.exception.RecommendationServiceException;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.UserRepository;

@Service
public class RecommendationService {

    private final RecommendationClient recommendationClient;
    private final PurchaseLineRepository purchaseLineRepository;
    private final UserRepository userRepository;

    public RecommendationService(RecommendationClient recommendationClient,
            PurchaseLineRepository purchaseLineRepository,
            UserRepository userRepository) {
        this.recommendationClient = recommendationClient;
        this.purchaseLineRepository = purchaseLineRepository;
        this.userRepository = userRepository;
    }

    public RecommendationResponse getRecommendations(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        }

        Map<Long, Double> purchasesByGame = purchaseLineRepository.findByPurchaseUserId(userId).stream()
                .map(PurchaseLine::getGame)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Game::getId,
                        game -> defaultRating(game.getRating()),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        List<UserPurchaseDto> purchases = purchasesByGame.entrySet().stream()
                .map(entry -> new UserPurchaseDto(entry.getKey(), entry.getValue()))
                .toList();

        RecommendationPayload payload = new RecommendationPayload(userId, purchases);
        try {
            return recommendationClient.fetchRecommendations(payload);
        } catch (RecommendationServiceException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, ex.getMessage(), ex);
        }
    }

    private double defaultRating(Double rating) {
        return rating != null ? rating : 5.0;
    }
}

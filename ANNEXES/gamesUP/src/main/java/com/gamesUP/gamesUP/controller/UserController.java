package com.gamesUP.gamesUP.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.recommendation.dto.RecommendationResponse;
import com.gamesUP.gamesUP.recommendation.service.RecommendationService;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
            RecommendationService recommendationService,
            UserRepository userRepository) {
        this.userService = userService;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/{userId}/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        ensureAccess(userId, authentication);
        RecommendationResponse response = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }

    private void ensureAccess(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }

        Long authenticatedUserId = userRepository.findByEmail(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));

        if (!authenticatedUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vous ne pouvez consulter que vos propres recommandations");
        }
    }
}

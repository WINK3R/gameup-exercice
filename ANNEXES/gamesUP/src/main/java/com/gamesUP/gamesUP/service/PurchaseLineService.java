package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.UserRepository;

@Service
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;
    private final UserRepository userRepository;

    public PurchaseLineService(PurchaseLineRepository purchaseLineRepository, UserRepository userRepository) {
        this.purchaseLineRepository = purchaseLineRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PurchaseLine> getAll() {
        return purchaseLineRepository.findAll();
    }

    public List<PurchaseLine> getMine(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return purchaseLineRepository.findByPurchaseUserId(userId);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return user.getId();
    }
}

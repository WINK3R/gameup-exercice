package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.model.Purchase;
import com.gamesUP.gamesUP.repository.PurchaseRepository;


@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CurrentUserService currentUserService;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, CurrentUserService currentUserService) {
        this.purchaseRepository = purchaseRepository;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<Purchase> getAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public List<Purchase> getMine(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        return purchaseRepository.findByUserId(userId);
    }
}

package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;

@Service
public class PurchaseLineServiceImpl implements PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;
    private final CurrentUserService currentUserService;

    public PurchaseLineServiceImpl(PurchaseLineRepository purchaseLineRepository, CurrentUserService currentUserService) {
        this.purchaseLineRepository = purchaseLineRepository;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<PurchaseLine> getAll() {
        return purchaseLineRepository.findAll();
    }

    @Override
    public List<PurchaseLine> getMine(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        return purchaseLineRepository.findByPurchaseUserId(userId);
    }
}

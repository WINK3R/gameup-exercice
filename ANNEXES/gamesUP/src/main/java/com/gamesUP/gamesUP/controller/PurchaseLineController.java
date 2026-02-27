package com.gamesUP.gamesUP.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.service.PurchaseLineService;

@RestController
@RequestMapping("/api/purchase-lines")
public class PurchaseLineController {

    private final PurchaseLineService purchaseLineService;

    public PurchaseLineController(PurchaseLineService purchaseLineService) {
        this.purchaseLineService = purchaseLineService;
    }

    @GetMapping
    public List<PurchaseLine> getAll() {
        return purchaseLineService.getAll();
    }

    @GetMapping("/me")
    public List<PurchaseLine> getMine(Authentication authentication) {
        return purchaseLineService.getMine(authentication);
    }
}

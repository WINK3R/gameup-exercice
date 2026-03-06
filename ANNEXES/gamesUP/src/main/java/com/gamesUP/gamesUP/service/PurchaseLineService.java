package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.gamesUP.gamesUP.model.PurchaseLine;

public interface PurchaseLineService {

    List<PurchaseLine> getAll();

    List<PurchaseLine> getMine(Authentication authentication);
}

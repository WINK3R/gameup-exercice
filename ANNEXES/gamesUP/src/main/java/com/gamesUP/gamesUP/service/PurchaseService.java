package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.gamesUP.gamesUP.model.Purchase;

public interface PurchaseService {

    List<Purchase> getAll();

    List<Purchase> getMine(Authentication authentication);
}

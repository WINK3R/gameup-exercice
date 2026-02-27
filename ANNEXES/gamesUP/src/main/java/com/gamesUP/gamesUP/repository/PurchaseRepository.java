package com.gamesUP.gamesUP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamesUP.gamesUP.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
}

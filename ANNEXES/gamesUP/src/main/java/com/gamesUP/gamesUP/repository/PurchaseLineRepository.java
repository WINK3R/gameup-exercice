package com.gamesUP.gamesUP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamesUP.gamesUP.model.PurchaseLine;

public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, Long> {
    List<PurchaseLine> findByPurchaseUserId(Long userId);
}

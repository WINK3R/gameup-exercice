package com.gamesUP.gamesUP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamesUP.gamesUP.model.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);
    boolean existsByUserIdAndGameId(Long userId, Long gameId);
    void deleteByUserIdAndGameId(Long userId, Long gameId);
}

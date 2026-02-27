package com.gamesUP.gamesUP.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.model.Wishlist;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public WishlistService(WishlistRepository wishlistRepository,
            UserRepository userRepository,
            GameRepository gameRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<Wishlist> findByUser(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public Wishlist addToWishlist(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur inconnu"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jeu inconnu"));

        if (wishlistRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce jeu est déjà dans la liste de souhaits");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setGame(game);
        wishlist.setCreatedAt(LocalDateTime.now());
        return wishlistRepository.save(wishlist);
    }

    public void removeFromWishlist(Long userId, Long gameId) {
        if (!wishlistRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée introuvable dans la wishlist");
        }
        wishlistRepository.deleteByUserIdAndGameId(userId, gameId);
    }
}

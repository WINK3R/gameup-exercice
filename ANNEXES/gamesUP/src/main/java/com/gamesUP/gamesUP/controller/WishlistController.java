package com.gamesUP.gamesUP.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.dto.WishlistRequest;
import com.gamesUP.gamesUP.model.Wishlist;
import com.gamesUP.gamesUP.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{userId}")
    public List<Wishlist> list(@PathVariable Long userId) {
        return wishlistService.findByUser(userId);
    }

    @PostMapping
    public Wishlist add(@RequestBody WishlistRequest request) {
        if (request == null || request.getUserId() == null || request.getGameId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId et gameId sont requis");
        }
        return wishlistService.addToWishlist(request.getUserId(), request.getGameId());
    }

    @DeleteMapping
    public void remove(@RequestParam Long userId, @RequestParam Long gameId) {
        wishlistService.removeFromWishlist(userId, gameId);
    }
}

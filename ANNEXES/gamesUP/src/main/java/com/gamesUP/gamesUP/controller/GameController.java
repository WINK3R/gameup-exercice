package com.gamesUP.gamesUP.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.service.GameService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public Page<Game> searchGames(@ModelAttribute GameSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sortConfig = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        int sanitizedSize = Math.max(1, Math.min(size, 100));
        int sanitizedPage = Math.max(0, page);
        Pageable pageable = PageRequest.of(sanitizedPage, sanitizedSize, sortConfig);
        return gameService.search(searchRequest, pageable);
    }

    @GetMapping("/{id}")
    public Game getGame(@PathVariable Long id) {
        return gameService.getById(id);
    }

    @PostMapping
    public Game createGame(@RequestBody GameRequest request) {
        return gameService.create(request);
    }

    @PutMapping("/{id}")
    public Game updateGame(@PathVariable Long id, @RequestBody GameRequest request) {
        return gameService.update(id, request);
    }

    @PatchMapping("/{id}/stock")
    public Game updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        return gameService.updateStock(id, stock);
    }

    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable Long id) {
        gameService.delete(id);
    }
}

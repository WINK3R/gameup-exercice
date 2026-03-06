package com.gamesUP.gamesUP.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Game;

public interface GameService {

    Page<Game> search(GameSearchRequest criteria, Pageable pageable);

    Game create(GameRequest request);

    Game update(Long id, GameRequest request);

    Game updateStock(Long id, Integer stock);

    Game getById(Long id);

    void delete(Long id);
}

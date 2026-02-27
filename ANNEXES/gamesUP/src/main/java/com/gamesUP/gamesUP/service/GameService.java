package com.gamesUP.gamesUP.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import com.gamesUP.gamesUP.repository.specification.GameSpecifications;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;

    public GameService(GameRepository gameRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            AuthorRepository authorRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public Page<Game> search(GameSearchRequest criteria, Pageable pageable) {
        Specification<Game> spec = GameSpecifications.withSearch(criteria);
        return gameRepository.findAll(spec, pageable);
    }

    public Game create(GameRequest request) {
        Game game = new Game();
        applyRequestToEntity(game, request);
        return gameRepository.save(game);
    }

    public Game update(Long id, GameRequest request) {
        Game existing = getById(id);
        applyRequestToEntity(existing, request);
        return gameRepository.save(existing);
    }

    public Game updateStock(Long id, Integer stock) {
        if (stock == null || stock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le stock doit être positif");
        }
        Game game = getById(id);
        game.setStock(stock);
        return gameRepository.save(game);
    }

    @Transactional(readOnly = true)
    public Game getById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jeu introuvable"));
    }

    public void delete(Long id) {
        Game game = getById(id);
        gameRepository.delete(game);
    }

    private void applyRequestToEntity(Game game, GameRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les données du jeu sont requises");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre du jeu est obligatoire");
        }
        game.setTitle(request.getTitle().trim());
        game.setDescription(request.getDescription());
        game.setGenre(request.getGenre());
        if (request.getPrice() != null && request.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix doit être positif");
        }
        game.setPrice(request.getPrice());

        if (request.getMinPlayers() != null && request.getMinPlayers() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre minimal de joueurs doit être supérieur à 0");
        }
        game.setMinPlayers(request.getMinPlayers());

        if (request.getMaxPlayers() != null && request.getMinPlayers() != null
                && request.getMaxPlayers() < request.getMinPlayers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nombre maximal de joueurs doit être supérieur au minimum");
        }
        game.setMaxPlayers(request.getMaxPlayers());
        game.setAverageDuration(request.getAverageDuration());
        game.setReleaseDate(request.getReleaseDate());
        game.setRating(request.getRating());
        if (request.getStock() != null && request.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le stock doit être positif");
        }
        game.setStock(request.getStock());

        game.setCategory(resolveCategory(request.getCategoryId()));
        game.setPublisher(resolvePublisher(request.getPublisherId()));
        game.setAuthor(resolveAuthor(request.getAuthorId()));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catégorie inconnue"));
    }

    private Publisher resolvePublisher(Long publisherId) {
        if (publisherId == null) {
            return null;
        }
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Éditeur inconnu"));
    }

    private Author resolveAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auteur inconnu"));
    }
}

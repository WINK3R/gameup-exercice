package com.gamesUP.gamesUP.repository.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.repository.GameRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
class GameSpecificationsTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void withSearchShouldApplyAllFilters() {
        Category category = persistCategory("Strategy", "strategy");
        Publisher publisher = persistPublisher("Space Cowboys");
        Author author = persistAuthor("Jacob Fryxelius");

        Game matching = buildGame("Terraforming Mars", "Colonize Mars", "engine", new BigDecimal("60"), 1, 5, 4.8,
                10, LocalDate.of(2017, 1, 1));
        matching.setCategory(category);
        matching.setPublisher(publisher);
        matching.setAuthor(author);
        gameRepository.save(matching);

        Game other = buildGame("Azul", "Abstract puzzle", "abstract", new BigDecimal("30"), 2, 4, 3.0, 0,
                LocalDate.of(2015, 1, 1));
        other.setCategory(category);
        other.setPublisher(publisher);
        other.setAuthor(author);
        gameRepository.save(other);

        GameSearchRequest criteria = new GameSearchRequest();
        criteria.setKeyword("mars");
        criteria.setGenre("engine");
        criteria.setCategoryId(category.getId());
        criteria.setPublisherId(publisher.getId());
        criteria.setAuthorId(author.getId());
        criteria.setMinPrice(new BigDecimal("50"));
        criteria.setMaxPrice(new BigDecimal("70"));
        criteria.setMinPlayers(1);
        criteria.setMaxPlayers(5);
        criteria.setMinRating(4.0);
        criteria.setInStock(true);
        criteria.setReleasedAfter(LocalDate.of(2016, 1, 1));
        criteria.setReleasedBefore(LocalDate.of(2018, 1, 1));

        List<Game> results = gameRepository.findAll(GameSpecifications.withSearch(criteria));

        assertThat(results).containsExactly(matching);
    }

    @Test
    void withSearchShouldReturnAllGamesWhenCriteriaMissing() {
        Game game = buildGame("Catan", "Classic", "family", new BigDecimal("45"), 3, 4, 4.0, 5,
                LocalDate.of(2010, 1, 1));
        gameRepository.save(game);

        List<Game> results = gameRepository.findAll(GameSpecifications.withSearch(null));

        assertThat(results).isNotEmpty();
    }

    private Category persistCategory(String name, String slug) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        entityManager.persist(category);
        return category;
    }

    private Publisher persistPublisher(String name) {
        Publisher publisher = new Publisher();
        publisher.setName(name);
        entityManager.persist(publisher);
        return publisher;
    }

    private Author persistAuthor(String fullName) {
        Author author = new Author();
        author.setFullName(fullName);
        entityManager.persist(author);
        return author;
    }

    private Game buildGame(String title, String description, String genre, BigDecimal price, int minPlayers,
            int maxPlayers, double rating, int stock, LocalDate releaseDate) {
        Game game = new Game();
        game.setTitle(title);
        game.setDescription(description);
        game.setGenre(genre);
        game.setPrice(price);
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);
        game.setRating(rating);
        game.setStock(stock);
        game.setReleaseDate(releaseDate);
        return game;
    }
}

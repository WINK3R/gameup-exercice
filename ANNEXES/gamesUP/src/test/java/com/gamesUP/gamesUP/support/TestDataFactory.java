package com.gamesUP.gamesUP.support;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.model.Purchase;
import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.model.User;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static GameRequest createGameRequest() {
        GameRequest request = new GameRequest();
        request.setTitle(" Terraforming Mars ");
        request.setDescription("Colonisez Mars");
        request.setGenre("Strategy");
        request.setPrice(BigDecimal.valueOf(59.99));
        request.setMinPlayers(1);
        request.setMaxPlayers(5);
        request.setAverageDuration(120);
        request.setReleaseDate(LocalDate.of(2016, 9, 15));
        request.setRating(4.5);
        request.setStock(10);
        request.setCategoryId(1L);
        request.setPublisherId(2L);
        request.setAuthorId(3L);
        return request;
    }

    public static Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("Strategie");
        category.setSlug("strategie");
        category.setDescription("Jeux de stratégie");
        return category;
    }

    public static Publisher createPublisher(Long id) {
        Publisher publisher = new Publisher();
        publisher.setId(id);
        publisher.setName("Space Cowboys");
        publisher.setCountry("FR");
        publisher.setWebsite("https://spacecowboys.fr");
        return publisher;
    }

    public static Author createAuthor(Long id) {
        Author author = new Author();
        author.setId(id);
        author.setFullName("Jacob Fryxelius");
        author.setCountry("SE");
        author.setBiography("Designer connu");
        return author;
    }

    public static Game createGame(String title) {
        Game game = new Game();
        game.setId(99L);
        game.setTitle(title);
        game.setPrice(BigDecimal.valueOf(29.99));
        game.setStock(5);
        return game;
    }

    public static User createUser(Long id, String email, User.Role role) {
        User user = new User();
        user.setId(id);
        user.setDisplayName("Test User");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(role);
        return user;
    }

    public static Purchase createPurchase(User user) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setDate(LocalDateTime.now());
        purchase.setPaid(true);
        purchase.setDelivered(false);
        purchase.setArchived(false);
        return purchase;
    }

    public static PurchaseLine createPurchaseLine(Purchase purchase, Game game, int quantity) {
        PurchaseLine line = new PurchaseLine();
        line.setPurchase(purchase);
        line.setGame(game);
        line.setQuantity(quantity);
        line.setPrix(BigDecimal.valueOf(49.90));
        return line;
    }
}

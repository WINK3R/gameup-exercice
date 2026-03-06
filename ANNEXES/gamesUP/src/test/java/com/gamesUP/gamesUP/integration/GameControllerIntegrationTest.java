package com.gamesUP.gamesUP.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.repository.ReviewRepository;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PurchaseLineRepository purchaseLineRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;

    private Category category;
    private Publisher publisher;
    private Author author;

    @BeforeEach
    void setUp() {
        purchaseLineRepository.deleteAll();
        purchaseRepository.deleteAll();
        reviewRepository.deleteAll();
        gameRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        categoryRepository.deleteAll();
        category = categoryRepository.save(newCategory());
        publisher = publisherRepository.save(newPublisher());
        author = authorRepository.save(newAuthor());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGameEndpointShouldPersistGame() throws Exception {
        GameRequest request = buildRequest();
        request.setCategoryId(category.getId());
        request.setPublisherId(publisher.getId());
        request.setAuthorId(author.getId());

        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(gameRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchEndpointShouldReturnPage() throws Exception {
        Game game = new Game();
        game.setTitle("Terraforming");
        game.setGenre("Strategy");
        gameRepository.save(game);

        mockMvc.perform(get("/api/games")
                .param("genre", "Strategy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Terraforming"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchStockShouldUpdateGame() throws Exception {
        Game game = new Game();
        game.setTitle("Azul");
        gameRepository.save(game);

        mockMvc.perform(patch("/api/games/" + game.getId() + "/stock")
                .param("stock", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(12));
    }

    private GameRequest buildRequest() {
        GameRequest request = new GameRequest();
        request.setTitle("Wingspan");
        request.setDescription("Manage birds");
        request.setGenre("Strategy");
        request.setPrice(BigDecimal.valueOf(55));
        request.setMinPlayers(1);
        request.setMaxPlayers(5);
        request.setAverageDuration(90);
        request.setReleaseDate(LocalDate.of(2020, 1, 1));
        request.setRating(4.7);
        request.setStock(10);
        return request;
    }

    private Category newCategory() {
        Category category = new Category();
        category.setName("Strategy");
        category.setSlug("strategy");
        category.setDescription("Tactical games");
        return category;
    }

    private Publisher newPublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("StoneMaier");
        return publisher;
    }

    private Author newAuthor() {
        Author author = new Author();
        author.setFullName("Elizabeth Hargrave");
        return author;
    }
}

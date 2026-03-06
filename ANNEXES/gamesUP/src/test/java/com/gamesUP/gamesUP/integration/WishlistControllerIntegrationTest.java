package com.gamesUP.gamesUP.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.WishlistRequest;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;

@SpringBootTest
@AutoConfigureMockMvc
class WishlistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private PurchaseLineRepository purchaseLineRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        wishlistRepository.deleteAll();
        purchaseLineRepository.deleteAll();
        purchaseRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(TestFixtures.user());
        game = gameRepository.save(TestFixtures.game());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void addAndListWishlistEntries() throws Exception {
        WishlistRequest request = new WishlistRequest();
        request.setUserId(user.getId());
        request.setGameId(game.getId());

        mockMvc.perform(post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(wishlistRepository.findByUserId(user.getId())).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void deleteWishlistEntry() throws Exception {
        wishlistRepository.save(TestFixtures.wishlist(user, game));

        mockMvc.perform(delete("/api/wishlist")
                .param("userId", user.getId().toString())
                .param("gameId", game.getId().toString()))
                .andExpect(status().isOk());

        assertThat(wishlistRepository.count()).isZero();
    }

    private static class TestFixtures {
        static User user() {
            User user = new User();
            user.setDisplayName("Client");
            user.setEmail("client@test.local");
            user.setPassword("pwd");
            user.setRole(User.Role.CLIENT);
            return user;
        }

        static Game game() {
            Game game = new Game();
            game.setTitle("Azul");
            game.setPrice(BigDecimal.valueOf(30));
            return game;
        }

        static com.gamesUP.gamesUP.model.Wishlist wishlist(User user, Game game) {
            com.gamesUP.gamesUP.model.Wishlist wishlist = new com.gamesUP.gamesUP.model.Wishlist();
            wishlist.setUser(user);
            wishlist.setGame(game);
            return wishlist;
        }
    }
}

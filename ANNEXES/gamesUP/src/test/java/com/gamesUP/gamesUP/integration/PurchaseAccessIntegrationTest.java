package com.gamesUP.gamesUP.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.Purchase;
import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.PurchaseLineService;
import com.gamesUP.gamesUP.service.PurchaseService;
import com.gamesUP.gamesUP.support.TestDataFactory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PurchaseAccessIntegrationTest {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PurchaseLineService purchaseLineService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseLineRepository purchaseLineRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MockMvc mockMvc;

    private User admin;
    private User client;

    @BeforeEach
    void setUp() {
        purchaseLineRepository.deleteAll();
        purchaseRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();

        admin = userRepository.save(TestDataFactory.createUser(null, "admin@test.local", User.Role.ADMIN));
        client = userRepository.save(TestDataFactory.createUser(null, "client@test.local", User.Role.CLIENT));

        Game game = new Game();
        game.setTitle("Catan");
        game.setPrice(BigDecimal.valueOf(44.99));
        game.setStock(100);
        game = gameRepository.save(game);

        purchaseRepository.save(buildPurchaseWithLine(admin, game, 1));
        purchaseRepository.save(buildPurchaseWithLine(client, game, 2));
    }

    private Purchase buildPurchaseWithLine(User owner, Game game, int quantity) {
        Purchase purchase = new Purchase();
        purchase.setUser(owner);
        purchase.setDate(LocalDateTime.now());
        PurchaseLine line = new PurchaseLine();
        line.setPurchase(purchase);
        line.setGame(game);
        line.setQuantity(quantity);
        line.setPrix(BigDecimal.valueOf(44.99));
        purchase.getLines().add(line);
        return purchase;
    }

    @Test
    @WithMockUser(username = "admin@test.local", roles = "ADMIN")
    void adminCanListAllPurchases() {
        List<Purchase> purchases = purchaseService.getAll();
        assertThat(purchases).hasSize(2);
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientCannotListAllPurchases() {
        assertThrows(AccessDeniedException.class, () -> purchaseService.getAll());
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientGetsOnlyOwnPurchases() {
        List<Purchase> purchases = purchaseService.getMine(SecurityContextHolder.getContext().getAuthentication());
        assertThat(purchases).hasSize(1);
        assertThat(purchases.get(0).getUser().getId()).isEqualTo(client.getId());
    }

    @Test
    @WithMockUser(username = "admin@test.local", roles = "ADMIN")
    void adminCanListAllPurchaseLines() {
        List<PurchaseLine> lines = purchaseLineService.getAll();
        assertThat(lines).hasSize(2);
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientCannotListAllPurchaseLines() {
        assertThrows(AccessDeniedException.class, () -> purchaseLineService.getAll());
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientGetsOnlyOwnPurchaseLines() {
        List<PurchaseLine> lines = purchaseLineService.getMine(SecurityContextHolder.getContext().getAuthentication());
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getPurchase().getUser().getId()).isEqualTo(client.getId());
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientIsForbiddenOnPurchaseEndpoint() throws Exception {
        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "client@test.local", roles = "CLIENT")
    void clientIsForbiddenOnPurchaseLineEndpoint() throws Exception {
        mockMvc.perform(get("/api/purchase-lines"))
                .andExpect(status().isForbidden());
    }
}

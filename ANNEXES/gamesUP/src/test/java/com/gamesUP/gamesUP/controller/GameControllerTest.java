package com.gamesUP.gamesUP.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.service.GameService;

@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @Test
    void searchGamesShouldSanitizePaginationParameters() throws Exception {
        Page<Game> page = new PageImpl<>(List.of(new Game()));
        when(gameService.search(any(GameSearchRequest.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/games")
                .param("page", "-5")
                .param("size", "250")
                .param("sort", "rating")
                .param("direction", "desc")
                .param("genre", "strategy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(gameService).search(any(GameSearchRequest.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(100);
        assertThat(pageable.getSort().getOrderFor("rating").getDirection().isDescending()).isTrue();
    }

    @Test
    void getGameShouldReturnEntity() throws Exception {
        Game game = new Game();
        game.setId(42L);
        when(gameService.getById(42L)).thenReturn(game);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/games/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void createGameShouldDelegateToService() throws Exception {
        GameRequest request = new GameRequest();
        request.setTitle("Azul");
        Game created = new Game();
        created.setId(5L);
        when(gameService.create(any(GameRequest.class))).thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void updateGameShouldDelegateToService() throws Exception {
        GameRequest request = new GameRequest();
        request.setTitle("Azul");
        Game updated = new Game();
        updated.setId(7L);
        when(gameService.update(eq(7L), any(GameRequest.class))).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/games/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void updateStockShouldUsePatchEndpoint() throws Exception {
        Game updated = new Game();
        updated.setId(9L);
        updated.setStock(99);
        when(gameService.updateStock(9L, 99)).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/games/9/stock")
                .param("stock", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(99));
    }

    @Test
    void deleteGameShouldReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/games/3"))
                .andExpect(status().isOk());

        verify(gameService).delete(3L);
    }
}

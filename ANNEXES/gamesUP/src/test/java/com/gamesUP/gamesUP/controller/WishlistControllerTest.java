package com.gamesUP.gamesUP.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.WishlistRequest;
import com.gamesUP.gamesUP.model.Wishlist;
import com.gamesUP.gamesUP.security.JwtService;
import com.gamesUP.gamesUP.service.WishlistService;

@WebMvcTest(controllers = WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private JwtService jwtService;

    @Test
    void listShouldReturnWishlistForUser() throws Exception {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(1L);
        when(wishlistService.findByUser(5L)).thenReturn(List.of(wishlist));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addShouldValidateRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldDelegateToService() throws Exception {
        WishlistRequest request = new WishlistRequest();
        request.setUserId(3L);
        request.setGameId(7L);
        Wishlist saved = new Wishlist();
        saved.setId(9L);
        when(wishlistService.addToWishlist(3L, 7L)).thenReturn(saved);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    void removeShouldCallService() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist")
                .param("userId", "4")
                .param("gameId", "8"))
                .andExpect(status().isOk());

        verify(wishlistService).removeFromWishlist(eq(4L), eq(8L));
    }
}

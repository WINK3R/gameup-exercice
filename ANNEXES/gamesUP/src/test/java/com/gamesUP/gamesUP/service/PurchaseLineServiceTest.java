package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseLineServiceTest {

    @Mock
    private PurchaseLineRepository purchaseLineRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PurchaseLineServiceImpl purchaseLineService;

    @Test
    void getAllShouldReturnRepositoryContent() {
        List<PurchaseLine> lines = List.of(new PurchaseLine());
        when(purchaseLineRepository.findAll()).thenReturn(lines);

        assertThat(purchaseLineService.getAll()).isSameAs(lines);
        verify(purchaseLineRepository).findAll();
    }

    @Test
    void getMineShouldReturnLinesForAuthenticatedUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("client@gamesup.test", "pwd");
        List<PurchaseLine> lines = List.of(new PurchaseLine());
        when(currentUserService.requireUserId(authentication)).thenReturn(1L);
        when(purchaseLineRepository.findByPurchaseUserId(1L)).thenReturn(lines);

        assertThat(purchaseLineService.getMine(authentication)).containsExactlyElementsOf(lines);
    }

    @Test
    void getMineShouldFailWhenUserMissing() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("ghost@test", "pwd");
        when(currentUserService.requireUserId(authentication))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.getMine(authentication));
    }
}

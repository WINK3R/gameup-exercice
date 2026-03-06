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

import com.gamesUP.gamesUP.model.Purchase;
import com.gamesUP.gamesUP.repository.PurchaseRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Test
    void getAllShouldReturnRepositoryContent() {
        List<Purchase> purchases = List.of(new Purchase());
        when(purchaseRepository.findAll()).thenReturn(purchases);

        assertThat(purchaseService.getAll()).isSameAs(purchases);
        verify(purchaseRepository).findAll();
    }

    @Test
    void getMineShouldReturnPurchaseForAuthenticatedUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("client@gamesup.test", "pwd");
        List<Purchase> purchases = List.of(new Purchase());
        when(currentUserService.requireUserId(authentication)).thenReturn(1L);
        when(purchaseRepository.findByUserId(1L)).thenReturn(purchases);

        List<Purchase> result = purchaseService.getMine(authentication);

        assertThat(result).containsExactlyElementsOf(purchases);
    }

    @Test
    void getMineShouldFailWhenUserCannotBeResolved() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("ghost@test", "pwd");
        when(currentUserService.requireUserId(authentication))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        assertThrows(ResponseStatusException.class, () -> purchaseService.getMine(authentication));
    }
}

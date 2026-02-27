package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.gamesUP.gamesUP.model.Purchase;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void getAllShouldReturnRepositoryContent() {
        List<Purchase> purchases = List.of(new Purchase());
        when(purchaseRepository.findAll()).thenReturn(purchases);

        assertThat(purchaseService.getAll()).isSameAs(purchases);
        verify(purchaseRepository).findAll();
    }

    @Test
    void getMineShouldReturnPurchaseForAuthenticatedUser() {
        User user = TestDataFactory.createUser(1L, "client@gamesup.test", User.Role.CLIENT);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "pwd");
        List<Purchase> purchases = List.of(TestDataFactory.createPurchase(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(purchaseRepository.findByUserId(1L)).thenReturn(purchases);

        List<Purchase> result = purchaseService.getMine(authentication);

        assertThat(result).containsExactlyElementsOf(purchases);
    }

    @Test
    void getMineShouldFailWhenUserCannotBeResolved() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("ghost@test", "pwd");
        when(userRepository.findByEmail("ghost@test")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> purchaseService.getMine(authentication));
    }
}

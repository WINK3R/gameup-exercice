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

import com.gamesUP.gamesUP.model.PurchaseLine;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class PurchaseLineServiceTest {

    @Mock
    private PurchaseLineRepository purchaseLineRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PurchaseLineService purchaseLineService;

    @Test
    void getAllShouldReturnRepositoryContent() {
        List<PurchaseLine> lines = List.of(new PurchaseLine());
        when(purchaseLineRepository.findAll()).thenReturn(lines);

        assertThat(purchaseLineService.getAll()).isSameAs(lines);
        verify(purchaseLineRepository).findAll();
    }

    @Test
    void getMineShouldReturnLinesForAuthenticatedUser() {
        User user = TestDataFactory.createUser(1L, "client@gamesup.test", User.Role.CLIENT);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "pwd");
        List<PurchaseLine> lines = List.of(new PurchaseLine());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(purchaseLineRepository.findByPurchaseUserId(1L)).thenReturn(lines);

        assertThat(purchaseLineService.getMine(authentication)).containsExactlyElementsOf(lines);
    }

    @Test
    void getMineShouldFailWhenUserMissing() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("ghost@test", "pwd");
        when(userRepository.findByEmail("ghost@test")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> purchaseLineService.getMine(authentication));
    }
}

package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.model.Wishlist;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void addToWishlistShouldPersistNewEntry() {
        User user = TestDataFactory.createUser(1L, "client@gamesup.test", User.Role.CLIENT);
        Game game = TestDataFactory.createGame("Azul");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));
        when(wishlistRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wishlist wishlist = wishlistService.addToWishlist(1L, 2L);

        assertThat(wishlist.getUser()).isEqualTo(user);
        assertThat(wishlist.getGame()).isEqualTo(game);
        verify(wishlistRepository).save(wishlist);
    }

    @Test
    void addToWishlistShouldFailWhenEntryExists() {
        User user = TestDataFactory.createUser(1L, "client@gamesup.test", User.Role.CLIENT);
        Game game = TestDataFactory.createGame("Azul");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));
        when(wishlistRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wishlistService.addToWishlist(1L, 2L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void addToWishlistShouldFailWhenUserUnknown() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wishlistService.addToWishlist(1L, 2L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addToWishlistShouldFailWhenGameUnknown() {
        User user = TestDataFactory.createUser(1L, "client@gamesup.test", User.Role.CLIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wishlistService.addToWishlist(1L, 2L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void removeFromWishlistShouldDeleteExistingEntry() {
        when(wishlistRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(true);

        wishlistService.removeFromWishlist(1L, 2L);

        verify(wishlistRepository).deleteByUserIdAndGameId(1L, 2L);
    }

    @Test
    void removeFromWishlistShouldFailWhenEntryMissing() {
        when(wishlistRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wishlistService.removeFromWishlist(1L, 2L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByUserDelegatesToRepository() {
        List<Wishlist> wishlists = List.of(new Wishlist());
        when(wishlistRepository.findByUserId(5L)).thenReturn(wishlists);

        assertThat(wishlistService.findByUser(5L)).isSameAs(wishlists);
    }
}

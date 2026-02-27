package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.dto.GameRequest;
import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Game;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void searchShouldDelegateToRepositoryWithSpecification() {
        GameSearchRequest criteria = new GameSearchRequest();
        criteria.setKeyword("mars");
        Pageable pageable = PageRequest.of(0, 5);
        Page<Game> expectedPage = new PageImpl<>(java.util.List.of(TestDataFactory.createGame("Terraforming")));
        when(gameRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable))).thenReturn(expectedPage);

        Page<Game> result = gameService.search(criteria, pageable);

        assertThat(result).isSameAs(expectedPage);
        ArgumentCaptor<org.springframework.data.jpa.domain.Specification<Game>> specCaptor = ArgumentCaptor.forClass(org.springframework.data.jpa.domain.Specification.class);
        verify(gameRepository).findAll(specCaptor.capture(), eq(pageable));
        assertThat(specCaptor.getValue()).isNotNull();
    }

    @Test
    void createShouldPersistGameWithResolvedRelations() {
        GameRequest request = TestDataFactory.createGameRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.createCategory(1L)));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(TestDataFactory.createPublisher(2L)));
        when(authorRepository.findById(3L)).thenReturn(Optional.of(TestDataFactory.createAuthor(3L)));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game created = gameService.create(request);

        assertThat(created.getTitle()).isEqualTo("Terraforming Mars");
        assertThat(created.getCategory()).isNotNull();
        assertThat(created.getPublisher()).isNotNull();
        assertThat(created.getAuthor()).isNotNull();
        verify(gameRepository).save(created);
    }

    @Test
    void createShouldRejectMissingTitle() {
        GameRequest request = new GameRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenRequestNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.create(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectNegativePrice() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setPrice(request.getPrice().negate());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectNegativeStockValue() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setStock(-5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectInvalidPlayerCounts() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setMinPlayers(0);

        ResponseStatusException minPlayersException = assertThrows(ResponseStatusException.class,
                () -> gameService.create(request));

        assertThat(minPlayersException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        request.setMinPlayers(2);
        request.setMaxPlayers(1);

        ResponseStatusException maxPlayersException = assertThrows(ResponseStatusException.class,
                () -> gameService.create(request));

        assertThat(maxPlayersException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectMinPlayersLessThanOne() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setMinPlayers(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectMaxPlayersLowerThanMin() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setMinPlayers(3);
        request.setMaxPlayers(2);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRejectNegativeStock() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setStock(-1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenRelationsMissing() {
        GameRequest request = TestDataFactory.createGameRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldAllowNullCategoryPublisherAuthor() {
        GameRequest request = TestDataFactory.createGameRequest();
        request.setCategoryId(null);
        request.setPublisherId(null);
        request.setAuthorId(null);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game created = gameService.create(request);

        assertThat(created.getCategory()).isNull();
        assertThat(created.getPublisher()).isNull();
        assertThat(created.getAuthor()).isNull();
    }

    @Test
    void createShouldFailWhenPublisherMissing() {
        GameRequest request = TestDataFactory.createGameRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.createCategory(1L)));
        when(publisherRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenAuthorMissing() {
        GameRequest request = TestDataFactory.createGameRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.createCategory(1L)));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(TestDataFactory.createPublisher(2L)));
        when(authorRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.create(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldPersistChangesOnExistingGame() {
        Game existing = TestDataFactory.createGame("Azul");
        when(gameRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.createCategory(1L)));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(TestDataFactory.createPublisher(2L)));
        when(authorRepository.findById(3L)).thenReturn(Optional.of(TestDataFactory.createAuthor(3L)));
        when(gameRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        GameRequest payload = TestDataFactory.createGameRequest();
        payload.setTitle("Updated title");
        payload.setStock(50);

        Game updated = gameService.update(5L, payload);

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getStock()).isEqualTo(50);
        assertThat(updated.getCategory()).isNotNull();
        verify(gameRepository).save(existing);
    }

    @Test
    void deleteShouldRemoveExistingGame() {
        Game existing = TestDataFactory.createGame("Azul");
        when(gameRepository.findById(7L)).thenReturn(Optional.of(existing));

        gameService.delete(7L);

        verify(gameRepository).delete(existing);
    }

    @Test
    void updateStockShouldUpdateEntityWhenValueIsValid() {
        Game game = TestDataFactory.createGame("Azul");
        when(gameRepository.findById(42L)).thenReturn(Optional.of(game));
        when(gameRepository.save(game)).thenReturn(game);

        Game updated = gameService.updateStock(42L, 8);

        assertThat(updated.getStock()).isEqualTo(8);
        verify(gameRepository).save(game);
    }

    @Test
    void updateStockShouldRejectNegativeValues() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.updateStock(1L, -1));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateStockShouldRejectNullValue() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.updateStock(1L, null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getByIdShouldThrowWhenGameIsMissing() {
        when(gameRepository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gameService.getById(404L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

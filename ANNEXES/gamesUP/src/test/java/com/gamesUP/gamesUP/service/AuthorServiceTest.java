package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void createShouldValidateAndSaveAuthor() {
        Author author = new Author();
        author.setFullName("Uwe Rosenberg");
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Author saved = authorService.create(author);

        assertThat(saved.getFullName()).isEqualTo("Uwe Rosenberg");
    }

    @Test
    void createShouldFailWhenNameMissing() {
        Author author = new Author();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorService.create(author));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenAuthorIsNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorService.create(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllShouldRequestSortedAuthors() {
        when(authorRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of(new Author()));

        authorService.findAll();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(authorRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("fullName")).isNotNull();
    }

    @Test
    void updateShouldThrowWhenAuthorNotFound() {
        when(authorRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorService.update(3L, new Author()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldCheckExistence() {
        when(authorRepository.existsById(9L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorService.delete(9L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldInvokeRepositoryWhenPresent() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.delete(1L);

        verify(authorRepository).deleteById(1L);
    }

    @Test
    void updateShouldTrimNameBeforeSaving() {
        Author existing = new Author();
        existing.setId(5L);
        existing.setFullName("Old");
        when(authorRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(authorRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Author payload = new Author();
        payload.setFullName("  New Name  ");

        Author updated = authorService.update(5L, payload);

        assertThat(updated.getFullName()).isEqualTo("New Name");
        verify(authorRepository).save(existing);
    }
}

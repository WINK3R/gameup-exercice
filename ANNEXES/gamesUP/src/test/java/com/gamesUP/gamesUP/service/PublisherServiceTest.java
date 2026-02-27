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

import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.repository.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void createShouldTrimNameAndPersist() {
        Publisher publisher = new Publisher();
        publisher.setName("  Repos Production  ");
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Publisher saved = publisherService.create(publisher);

        assertThat(saved.getName()).isEqualTo("Repos Production");
    }

    @Test
    void createShouldFailWhenNameMissing() {
        Publisher publisher = new Publisher();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> publisherService.create(publisher));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenPublisherNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> publisherService.create(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllShouldRequestSortedPublishers() {
        when(publisherRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of(new Publisher()));

        publisherService.findAll();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(publisherRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("name")).isNotNull();
    }

    @Test
    void updateShouldThrowWhenPublisherNotFound() {
        when(publisherRepository.findById(8L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> publisherService.update(8L, new Publisher()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldCheckExistence() {
        when(publisherRepository.existsById(4L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> publisherService.delete(4L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldInvokeRepositoryWhenPresent() {
        when(publisherRepository.existsById(2L)).thenReturn(true);

        publisherService.delete(2L);

        verify(publisherRepository).deleteById(2L);
    }

    @Test
    void updateShouldTrimNameBeforeSaving() {
        Publisher existing = new Publisher();
        existing.setId(4L);
        existing.setName("Old");
        when(publisherRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(publisherRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Publisher payload = new Publisher();
        payload.setName("  NewName  ");

        Publisher updated = publisherService.update(4L, payload);

        assertThat(updated.getName()).isEqualTo("NewName");
        verify(publisherRepository).save(existing);
    }
}

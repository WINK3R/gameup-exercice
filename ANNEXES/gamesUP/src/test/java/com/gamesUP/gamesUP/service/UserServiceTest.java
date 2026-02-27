package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldEncodePasswordAndPersistUser() {
        User user = TestDataFactory.createUser(null, "Test@Example.com ", User.Role.CLIENT);
        user.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        User created = userService.create(user);

        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getPassword()).isEqualTo("hashed");
        assertThat(created.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        User user = TestDataFactory.createUser(null, "duplicate@example.com", User.Role.CLIENT);
        user.setPassword("pwd");
        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(new User()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(user));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createShouldValidateMandatoryFields() {
        User user = new User();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(user));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldRequireRole() {
        User user = new User();
        user.setDisplayName("Test");
        user.setEmail("test@example.com");
        user.setPassword("pwd");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(user));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenUserNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenPasswordMissingForNewUser() {
        User user = TestDataFactory.createUser(null, "missingpwd@test", User.Role.CLIENT);
        user.setPassword(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(user));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllShouldUseSortedRepositoryCall() {
        when(userRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of(TestDataFactory.createUser(1L, "user@test", User.Role.CLIENT)));

        userService.findAll();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(userRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("displayName")).isNotNull();
    }

    @Test
    void updateShouldEncodePasswordWhenProvided() {
        User existing = TestDataFactory.createUser(1L, "client@example.com", User.Role.CLIENT);
        existing.setPassword("old");
        User payload = TestDataFactory.createUser(1L, "client@example.com", User.Role.ADMIN);
        payload.setPassword("new");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("new")).thenReturn("encoded");
        when(userRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.update(1L, payload);

        assertThat(updated.getRole()).isEqualTo(User.Role.ADMIN);
        assertThat(updated.getPassword()).isEqualTo("encoded");
        verify(userRepository).save(existing);
    }

    @Test
    void updateShouldFailWhenUserMissing() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.update(42L, TestDataFactory.createUser(null, "client@example.com", User.Role.CLIENT)));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateShouldRejectDuplicateEmail() {
        User existing = TestDataFactory.createUser(1L, "client@example.com", User.Role.CLIENT);
        User payload = TestDataFactory.createUser(1L, "other@example.com", User.Role.CLIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(TestDataFactory.createUser(2L, "other@example.com", User.Role.CLIENT)));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.update(1L, payload));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateShouldRejectDisplayNameOrEmailMissing() {
        User existing = TestDataFactory.createUser(1L, "client@example.com", User.Role.CLIENT);
        User payload = new User();
        payload.setRole(User.Role.CLIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.update(1L, payload));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldKeepPasswordWhenBlank() {
        User existing = TestDataFactory.createUser(1L, "client@example.com", User.Role.CLIENT);
        existing.setPassword("hashed");
        User payload = TestDataFactory.createUser(1L, "client@example.com", User.Role.CLIENT);
        payload.setPassword("  ");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.update(1L, payload);

        assertThat(updated.getPassword()).isEqualTo("hashed");
    }

    @Test
    void deleteShouldRemoveExistingUser() {
        when(userRepository.existsById(7L)).thenReturn(true);

        userService.delete(7L);

        verify(userRepository).deleteById(7L);
    }

    @Test
    void deleteShouldFailWhenUserDoesNotExist() {
        when(userRepository.existsById(7L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.delete(7L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

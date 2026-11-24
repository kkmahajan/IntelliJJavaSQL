package org.example.automation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository repository;
    private UserService service;
    private final UserController.User seed = new UserController.User(1, "John Watson", "john@example.com");

    @BeforeEach void setUp() {
        repository = mock(UserRepository.class);
        when(repository.findAll()).thenReturn(List.of(seed));
        service = new UserService(repository);
    }

    @Test void listsAndFetchesUsers() {
        when(repository.findById(1)).thenReturn(Optional.of(seed));
        assertEquals("John Watson", service.get(1).name());
        assertEquals(1, service.list().size());
    }

    @Test void createsTrimmedUser() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UserController.User user = service.create(new UserController.UserInput(" Alice ", " alice@example.com "));
        assertEquals(2, user.id());
        assertEquals("Alice", user.name());
        assertEquals("alice@example.com", user.email());
        verify(repository).save(user);
    }

    @Test void updatesExistingUser() {
        when(repository.findById(1)).thenReturn(Optional.of(seed));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UserController.User updated = service.update(1, new UserController.UserInput("Updated", "john@example.com"));
        assertEquals("Updated", updated.name());
        verify(repository).save(updated);
    }

    @Test void deletesExistingUser() {
        when(repository.deleteById(1)).thenReturn(true);
        service.delete(1);
        verify(repository).deleteById(1);
    }

    @Test void missingUserReturnsNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertStatus(HttpStatus.NOT_FOUND, () -> service.get(99));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.update(99, new UserController.UserInput("X", "x@example.com")));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.delete(99));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid", "a@", "@b.com", "a b@example.com"})
    void rejectsInvalidEmail(String email) {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new UserController.UserInput("Alice", email)));
        verify(repository, never()).save(any());
    }

    @Test void rejectsBlankNameAndNullInput() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new UserController.UserInput(" ", "alice@example.com")));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(null));
    }

    @Test void rejectsDuplicateEmailIgnoringCase() {
        assertStatus(HttpStatus.CONFLICT, () -> service.create(new UserController.UserInput("Duplicate", "JOHN@example.com")));
        verify(repository, never()).save(any());
    }

    private void assertStatus(HttpStatus status, org.junit.jupiter.api.function.Executable action) {
        assertEquals(status, assertThrows(ResponseStatusException.class, action).getStatusCode());
    }
}

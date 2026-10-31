package org.example.automation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {
    private TicketRepository repository;
    private UserRepository users;
    private TicketService service;
    private final TicketController.TicketInput valid = new TicketController.TicketInput("  Cannot sign in  ", " Password rejected ", "high", 1);

    @BeforeEach void setup() {
        repository = mock(TicketRepository.class);
        users = mock(UserRepository.class);
        when(users.findById(1)).thenReturn(Optional.of(new UserController.User(1, "John", "john@example.com")));
        when(repository.nextId()).thenReturn(42);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new TicketService(repository, users);
    }

    @Test void createsTicketWithDefaultsAndTrimmedFields() {
        var ticket = service.create(valid);
        assertEquals(42, ticket.id());
        assertEquals("Cannot sign in", ticket.title());
        assertEquals("Password rejected", ticket.description());
        assertEquals("HIGH", ticket.priority());
        assertEquals("OPEN", ticket.status());
        assertEquals(1, ticket.assigneeId());
    }

    @Test void allowsUnassignedTicketAndNullDescription() {
        var ticket = service.create(new TicketController.TicketInput("Help", null, "LOW", null));
        assertNull(ticket.assigneeId());
        assertEquals("", ticket.description());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void rejectsBlankTitles(String title) {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new TicketController.TicketInput(title, "x", "LOW", null)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"URGENT", "", "NORMAL"})
    void rejectsInvalidPriorities(String priority) {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new TicketController.TicketInput("Help", "", priority, null)));
    }

    @Test void rejectsUnknownAssignee() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new TicketController.TicketInput("Help", "", "LOW", 999)));
    }

    @Test void rejectsNullAndOversizedPayloads() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(null));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new TicketController.TicketInput("x".repeat(121), "", "LOW", null)));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.create(new TicketController.TicketInput("Help", "x".repeat(2001), "LOW", null)));
    }

    @Test void rejectsInvalidFilters() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.list("invalid", null));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.list(null, "urgent"));
    }

    @Test void filtersTicketsByStatusAndPriority() {
        when(repository.list()).thenReturn(java.util.List.of(
                new TicketController.Ticket(1,"A","","HIGH","OPEN",null),
                new TicketController.Ticket(2,"B","","LOW","RESOLVED",null)));
        assertEquals(1, service.list("open", "high").size());
        assertTrue(service.list("resolved", "high").isEmpty());
    }

    @Test void movesThroughAllowedStatuses() {
        var open = new TicketController.Ticket(42,"Help","","HIGH","OPEN",1);
        when(repository.get(42)).thenReturn(Optional.of(open));
        assertEquals("IN_PROGRESS", service.transition(42, new TicketController.StatusInput("in_progress")).status());
        when(repository.get(42)).thenReturn(Optional.of(new TicketController.Ticket(42,"Help","","HIGH","IN_PROGRESS",1)));
        assertEquals("RESOLVED", service.transition(42, new TicketController.StatusInput("RESOLVED")).status());
    }

    @Test void rejectsSkippedOrBackwardTransitions() {
        when(repository.get(42)).thenReturn(Optional.of(new TicketController.Ticket(42,"Help","","HIGH","OPEN",1)));
        assertStatus(HttpStatus.CONFLICT, () -> service.transition(42, new TicketController.StatusInput("RESOLVED")));
        when(repository.get(42)).thenReturn(Optional.of(new TicketController.Ticket(42,"Help","","HIGH","RESOLVED",1)));
        assertStatus(HttpStatus.CONFLICT, () -> service.transition(42, new TicketController.StatusInput("OPEN")));
    }

    @Test void rejectsInvalidStatus() {
        when(repository.get(42)).thenReturn(Optional.of(new TicketController.Ticket(42,"Help","","HIGH","OPEN",1)));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.transition(42, new TicketController.StatusInput("CLOSED")));
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.transition(42, null));
    }

    @Test void updatePreservesStatus() {
        when(repository.get(42)).thenReturn(Optional.of(new TicketController.Ticket(42,"Old","","LOW","IN_PROGRESS",1)));
        var result = service.update(42, new TicketController.TicketInput("New", "Changed", "HIGH", null));
        assertEquals("IN_PROGRESS", result.status());
        assertEquals("New", result.title());
        assertNull(result.assigneeId());
    }

    @Test void missingTicketReturns404() {
        assertStatus(HttpStatus.NOT_FOUND, () -> service.get(999));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.update(999, valid));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.transition(999, new TicketController.StatusInput("IN_PROGRESS")));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.delete(999));
    }

    @Test void deleteExistingTicket() {
        when(repository.delete(42)).thenReturn(true);
        service.delete(42);
        verify(repository).delete(42);
    }

    private void assertStatus(HttpStatus expected, org.junit.jupiter.api.function.Executable action) {
        assertEquals(expected, assertThrows(ResponseStatusException.class, action).getStatusCode());
    }
}

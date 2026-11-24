package org.example.automation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class TicketService {
    private static final Set<String> PRIORITIES = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> STATUSES = Set.of("OPEN", "IN_PROGRESS", "RESOLVED");
    private final TicketRepository tickets;
    private final UserRepository users;

    public TicketService(TicketRepository tickets, UserRepository users) {
        this.tickets = tickets;
        this.users = users;
    }

    public List<TicketController.Ticket> list(String status, String priority) {
        String normalizedStatus = optionalFilter(status, STATUSES, "status");
        String normalizedPriority = optionalFilter(priority, PRIORITIES, "priority");
        return tickets.list().stream()
                .filter(t -> normalizedStatus == null || t.status().equals(normalizedStatus))
                .filter(t -> normalizedPriority == null || t.priority().equals(normalizedPriority))
                .toList();
    }

    public TicketController.Ticket get(int id) {
        return tickets.get(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public TicketController.Ticket create(TicketController.TicketInput input) {
        validate(input);
        return tickets.save(new TicketController.Ticket(tickets.nextId(), input.title().trim(),
                description(input.description()), normalize(input.priority()), "OPEN", input.assigneeId()));
    }

    public TicketController.Ticket update(int id, TicketController.TicketInput input) {
        TicketController.Ticket existing = get(id);
        validate(input);
        return tickets.save(new TicketController.Ticket(id, input.title().trim(),
                description(input.description()), normalize(input.priority()), existing.status(), input.assigneeId()));
    }

    public TicketController.Ticket transition(int id, TicketController.StatusInput input) {
        TicketController.Ticket ticket = get(id);
        if (input == null || input.status() == null || !STATUSES.contains(normalize(input.status()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid status required");
        }
        String next = normalize(input.status());
        boolean allowed = ticket.status().equals(next)
                || (ticket.status().equals("OPEN") && next.equals("IN_PROGRESS"))
                || (ticket.status().equals("IN_PROGRESS") && next.equals("RESOLVED"));
        if (!allowed) throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid status transition");
        return tickets.save(new TicketController.Ticket(ticket.id(), ticket.title(), ticket.description(),
                ticket.priority(), next, ticket.assigneeId()));
    }

    public void delete(int id) {
        if (!tickets.delete(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
    }

    private void validate(TicketController.TicketInput input) {
        if (input == null || input.title() == null || input.title().isBlank() || input.title().trim().length() > 120
                || input.description() != null && input.description().length() > 2000
                || input.priority() == null || !PRIORITIES.contains(normalize(input.priority()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid title, description and priority required");
        }
        if (input.assigneeId() != null && users.findById(input.assigneeId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee does not exist");
        }
    }

    private String optionalFilter(String value, Set<String> allowed, String field) {
        if (value == null || value.isBlank()) return null;
        String normalized = normalize(value);
        if (!allowed.contains(normalized)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + field);
        return normalized;
    }
    private String normalize(String value) { return value.trim().toUpperCase(Locale.ROOT); }
    private String description(String value) { return value == null ? "" : value.trim(); }
}

package org.example.automation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    public record Ticket(int id, String title, String description, String priority, String status, Integer assigneeId) {}
    public record TicketInput(String title, String description, String priority, Integer assigneeId) {}
    public record StatusInput(String status) {}
    private final TicketService service;
    public TicketController(TicketService service) { this.service = service; }

    @GetMapping
    public List<Ticket> list(@RequestParam(required = false) String status,
                             @RequestParam(required = false) String priority) {
        return service.list(status, priority);
    }
    @GetMapping("/{id}")
    public Ticket get(@PathVariable int id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<Ticket> create(@RequestBody(required = false) TicketInput input) {
        Ticket ticket = service.create(input);
        return ResponseEntity.created(URI.create("/api/tickets/" + ticket.id())).body(ticket);
    }
    @PutMapping("/{id}")
    public Ticket update(@PathVariable int id, @RequestBody(required = false) TicketInput input) {
        return service.update(id, input);
    }
    @PatchMapping("/{id}/status")
    public Ticket transition(@PathVariable int id, @RequestBody(required = false) StatusInput input) {
        return service.transition(id, input);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

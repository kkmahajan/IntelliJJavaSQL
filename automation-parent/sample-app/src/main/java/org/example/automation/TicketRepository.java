package org.example.automation;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TicketRepository {
    private final Map<Integer, TicketController.Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicInteger ids = new AtomicInteger();
    public List<TicketController.Ticket> list() {
        return tickets.values().stream().sorted(Comparator.comparingInt(TicketController.Ticket::id)).toList();
    }
    public Optional<TicketController.Ticket> get(int id) { return Optional.ofNullable(tickets.get(id)); }
    public TicketController.Ticket save(TicketController.Ticket ticket) { tickets.put(ticket.id(), ticket); return ticket; }
    public int nextId() { return ids.incrementAndGet(); }
    public boolean delete(int id) { return tickets.remove(id) != null; }
}

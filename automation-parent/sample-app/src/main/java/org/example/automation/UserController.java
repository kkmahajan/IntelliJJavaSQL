package org.example.automation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public UserController() {
        users.put(1, new User(1, "John Watson", "john@example.com"));
    }

    public record User(int id, String name, String email) {}
    public record UserInput(String name, String email) {}

    @GetMapping
    public List<User> users() {
        return users.values().stream().sorted(Comparator.comparingInt(User::id)).toList();
    }

    @GetMapping("/{id}")
    public User user(@PathVariable("id") int id) {
        User result = users.get(id);
        if (result == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return result;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody(required = false) UserInput input) {
        validate(input, null);
        int id = nextId.incrementAndGet();
        User created = new User(id, input.name().trim(), input.email().trim());
        users.put(id, created);
        return ResponseEntity.created(URI.create("/api/users/" + id)).body(created);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") int id, @RequestBody(required = false) UserInput input) {
        if (!users.containsKey(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        validate(input, id);
        User updated = new User(id, input.name().trim(), input.email().trim());
        users.put(id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        if (users.remove(id) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return ResponseEntity.noContent().build();
    }

    private void validate(UserInput input, Integer excludedId) {
        if (input == null || input.name() == null || input.name().isBlank()
                || input.email() == null || !EMAIL.matcher(input.email().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name and valid email are required");
        }
        String email = input.email().trim();
        if (users.values().stream().anyMatch(u -> !Integer.valueOf(u.id()).equals(excludedId)
                && u.email().equalsIgnoreCase(email))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }
}

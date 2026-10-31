package org.example.automation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    public record User(int id, String name, String email) {}
    public record UserInput(String name, String email) {}

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> users() { return service.list(); }

    @GetMapping("/{id}")
    public User user(@PathVariable("id") int id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody(required = false) UserInput input) {
        User created = service.create(input);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") int id, @RequestBody(required = false) UserInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

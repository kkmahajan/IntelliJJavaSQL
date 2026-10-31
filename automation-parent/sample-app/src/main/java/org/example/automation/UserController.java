package org.example.automation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public List<Map<String, Object>> users() {
        return List.of(Map.of("id", 1, "name", "John Watson", "email", "john@example.com"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> user(@PathVariable int id) {
        if (id != 1) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return users().getFirst();
    }
}

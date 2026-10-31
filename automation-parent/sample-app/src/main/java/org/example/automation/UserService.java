package org.example.automation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository repository;
    private final AtomicInteger nextId;

    public UserService(UserRepository repository) {
        this.repository = repository;
        int maxId = repository.findAll().stream().mapToInt(UserController.User::id).max().orElse(0);
        this.nextId = new AtomicInteger(maxId);
    }

    public List<UserController.User> list() { return repository.findAll(); }

    public UserController.User get(int id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserController.User create(UserController.UserInput input) {
        validate(input, null);
        return repository.save(new UserController.User(nextId.incrementAndGet(), input.name().trim(), input.email().trim()));
    }

    public UserController.User update(int id, UserController.UserInput input) {
        get(id);
        validate(input, id);
        return repository.save(new UserController.User(id, input.name().trim(), input.email().trim()));
    }

    public void delete(int id) {
        if (!repository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private void validate(UserController.UserInput input, Integer excludedId) {
        if (input == null || input.name() == null || input.name().isBlank()
                || input.email() == null || !EMAIL.matcher(input.email().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name and valid email are required");
        }
        String email = input.email().trim();
        if (repository.findAll().stream().anyMatch(u -> (excludedId == null || u.id() != excludedId)
                && u.email().equalsIgnoreCase(email))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }
}

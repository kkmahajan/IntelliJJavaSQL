package org.example.automation;

import org.springframework.stereotype.Repository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, UserController.User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        save(new UserController.User(1, "John Watson", "john@example.com"));
    }

    @Override public List<UserController.User> findAll() {
        return users.values().stream().sorted(Comparator.comparingInt(UserController.User::id)).toList();
    }
    @Override public Optional<UserController.User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }
    @Override public UserController.User save(UserController.User user) {
        users.put(user.id(), user);
        return user;
    }
    @Override public boolean deleteById(int id) {
        return users.remove(id) != null;
    }
}

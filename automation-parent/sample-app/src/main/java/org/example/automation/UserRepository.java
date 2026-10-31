package org.example.automation;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserController.User> findAll();
    Optional<UserController.User> findById(int id);
    UserController.User save(UserController.User user);
    boolean deleteById(int id);
}

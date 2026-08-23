package org.tracker.service;

import org.tracker.model.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(UUID id);
}

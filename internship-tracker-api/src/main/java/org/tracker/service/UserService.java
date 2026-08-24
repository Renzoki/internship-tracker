package org.tracker.service;

import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(UUID id);
    User createNewUser(CreateUserCommand command);
    User updateExistingUser(UpdateUserCommand command);
}

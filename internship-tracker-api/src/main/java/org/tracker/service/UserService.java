package org.tracker.service;

import jakarta.validation.Valid;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User createNewUser(@Valid CreateUserCommand command);
    User updateExistingUser(@Valid UpdateUserCommand command);
    void deleteUserById(UUID id);
}

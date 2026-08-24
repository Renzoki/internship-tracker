package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.exception.UserNotFoundException;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.repository.UserRepository;
import org.tracker.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
         this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User createNewUser(CreateUserCommand command) {
        User user = new User(
                command.firstName(),
                command.lastName(),
                command.email(),    // TODO: Check if pre-existing email exists
                command.password(), // TODO: Hash password using BCryptPasswordEncoder later
                Instant.now());

        return userRepository.save(user);
    }
}

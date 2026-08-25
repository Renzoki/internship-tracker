package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.exception.UserNotFoundException;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
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

    @Override
    public User updateExistingUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if(command.firstName() != null){
            user.setFirstName(command.firstName());
        }

        if(command.lastName() != null){
            user.setLastName(command.lastName());
        }

        if(command.email() != null){
            user.setEmail(command.email()); // TODO: Check if pre-existing email exists
        }

        if(command.password() != null){
            user.setPasswordHash(command.password()); // TODO: Hash password using BcryptPasswordEncoder later
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }
}

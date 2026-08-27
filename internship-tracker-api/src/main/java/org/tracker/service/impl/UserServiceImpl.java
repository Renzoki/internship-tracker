package org.tracker.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tracker.exception.EmailAlreadyExistsException;
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
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
         this.userRepository = userRepository;
         this.encoder = encoder;
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
        if(userRepository.existsByEmail(command.email())){
            throw new EmailAlreadyExistsException(command.email());
        }

        String hashedPassword = encoder.encode(command.password());

        User user = new User(
                command.firstName(),
                command.lastName(),
                command.email(),
                hashedPassword,
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
            if(userRepository.existsByEmail(command.email())){
                throw new EmailAlreadyExistsException(command.email());
            }
            user.setEmail(command.email());
        }

        if(command.password() != null){
            String hashedPassword = encoder.encode(command.password());
            user.setPasswordHash(hashedPassword);
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }
}

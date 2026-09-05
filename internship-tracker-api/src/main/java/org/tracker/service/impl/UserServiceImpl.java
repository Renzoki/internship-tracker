package org.tracker.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tracker.exception.EmailAlreadyExistsException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.repository.UserRepository;
import org.tracker.service.UserService;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, UserMapper mapper) {
         this.userRepository = userRepository;
         this.encoder = encoder;
         this.mapper = mapper;
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
        User user = mapper.toNewUser(command, hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public User updateExistingUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if(command.email() != null && !command.email().equalsIgnoreCase(user.getEmail())){
            if(userRepository.existsByEmail(command.email())){
                throw new EmailAlreadyExistsException(command.email());
            }
        }

        String hashedPassword = (command.password() != null)
                ? encoder.encode(command.password())
                : null;

        user = mapper.toUpdatedUser(user, command, hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }
}

package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.model.entities.User;
import org.tracker.repository.UserRepository;
import org.tracker.service.UserService;

import java.util.List;

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
}

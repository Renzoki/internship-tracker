package org.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tracker.mapper.UserMapper;
import org.tracker.model.entities.User;
import org.tracker.model.response.UserResponse;
import org.tracker.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper){
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> userList = userService.getAllUsers();
        List<UserResponse> response = userList.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}

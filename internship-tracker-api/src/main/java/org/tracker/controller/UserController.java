package org.tracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.tracker.configuration.UserPrincipal;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.model.request.CreateUserRequest;
import org.tracker.model.request.UpdateUserRequest;
import org.tracker.model.response.UserResponse;
import org.tracker.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper){
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/self")
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal UserPrincipal principal){
        System.out.println("Principal in controller: " + principal);
        User user = userService.getUserById(principal.id());
        UserResponse response = mapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createNewUser(
            @Valid @RequestBody CreateUserRequest request
    ){
        CreateUserCommand command = mapper.toCreateCommand(request);
        User user = userService.createNewUser(command);
        UserResponse response = mapper.toResponse(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/self")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserRequest request
    ){
        UpdateUserCommand command = mapper.toUpdateCommand(principal.id(), request);
        User user = userService.updateExistingUser(command);
        UserResponse response = mapper.toResponse(user);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/self")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserPrincipal principal){
        userService.deleteUserById(principal.id());
        return ResponseEntity.noContent().build();
    }
}

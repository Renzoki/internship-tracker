package org.tracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tracker.mapper.AuthMapper;
import org.tracker.model.request.LoginRequest;
import org.tracker.model.response.LoginResponse;
import org.tracker.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthMapper mapper;

    public AuthController(AuthService authService,
                          AuthMapper mapper
    ){
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> handleLogin(@Valid @RequestBody LoginRequest request){
        String token = authService.handleLogin(request);
        LoginResponse response = mapper.toResponse(token);
        return ResponseEntity.ok(response);
    }
}
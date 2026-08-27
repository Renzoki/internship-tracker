package org.tracker.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tracker.exception.AuthenticationErrorException;
import org.tracker.model.entities.User;
import org.tracker.model.request.LoginRequest;
import org.tracker.repository.UserRepository;
import org.tracker.service.AuthService;
import org.tracker.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder,
                           JwtService jwtService){
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    public String handleLogin(LoginRequest request){
        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(AuthenticationErrorException::new);

        if(!encoder.matches(request.password(), user.getPasswordHash())){
            throw new AuthenticationErrorException();
        }

        return jwtService.createJwtToken(user);
    }
}

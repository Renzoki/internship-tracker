package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.response.LoginResponse;

@Component
public class AuthMapper {
    public LoginResponse toResponse(String token){
        return new LoginResponse(token);
    }
}

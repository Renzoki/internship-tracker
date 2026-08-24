package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.model.request.CreateUserRequest;
import org.tracker.model.response.UserResponse;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public CreateUserCommand toCommand(CreateUserRequest request){
        return new CreateUserCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
    }
}

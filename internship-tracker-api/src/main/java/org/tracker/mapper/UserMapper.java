package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.model.request.CreateUserRequest;
import org.tracker.model.request.UpdateUserRequest;
import org.tracker.model.response.UserResponse;

import java.util.UUID;

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

    public CreateUserCommand toCreateCommand(CreateUserRequest request){
        return new CreateUserCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
    }

    public UpdateUserCommand toUpdateCommand(UUID id, UpdateUserRequest request){
        return new UpdateUserCommand(
                id,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
    }
}

package org.tracker;

import org.junit.jupiter.api.Test;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.model.request.CreateUserRequest;
import org.tracker.model.request.UpdateUserRequest;
import org.tracker.model.response.UserResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class UserMapperUnitTest {

    private final UserMapper userMapper = new UserMapper();
    private final UUID mockId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void toResponse_validUserObject_shouldReturnUserResponse() {
        User mockUser = new User(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com", null, null);
        UserResponse expected = new UserResponse(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com");

        UserResponse actual = userMapper.toResponse(mockUser);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse_emptyUserObject_shouldReturnEmptyUserResponse() {
        UserResponse expected = new UserResponse(null, null, null, null);

        UserResponse actual = userMapper.toResponse(new User());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toCreateCommand_validRequestBody_shouldReturnCreateUserCommand() {
        CreateUserRequest request = new CreateUserRequest("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        CreateUserCommand expected = new CreateUserCommand("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");

        CreateUserCommand actual = userMapper.toCreateCommand(request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toCreateCommand_nullRequestBody_shouldReturnNullCreateUserCommand() {
        CreateUserRequest request = new CreateUserRequest(null, null, null, null);
        CreateUserCommand expected = new CreateUserCommand(null, null, null, null);

        CreateUserCommand actual = userMapper.toCreateCommand(request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUserFromCreateCommand_validCommandBody_shouldReturnUser() {
        CreateUserCommand command = new CreateUserCommand("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        User expected = new User(null, "Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword", null);

        User actual = userMapper.toNewUser(command, "mockPassword");

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
        assertThat(actual.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void toUserFromCreateCommand_nullCommandBody_shouldReturnUser() {
        CreateUserCommand command = new CreateUserCommand(null, null, null, null);
        User expected = new User(null, null, null, null, null, null);

        User actual = userMapper.toNewUser(command, null);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toUpdateCommand_requestBodyHasNoNullFields_shouldReturnUpdateUserCommand() {
        UpdateUserRequest request = new UpdateUserRequest("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        UpdateUserCommand expected = new UpdateUserCommand(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");

        UpdateUserCommand actual = userMapper.toUpdateCommand(mockId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateCommand_hasPartiallyNullRequestBody_shouldReturnUpdateUserCommand() {
        UpdateUserRequest request = new UpdateUserRequest("Renz", null, "renzonifico@gmail.com", null);
        UpdateUserCommand expected = new UpdateUserCommand(mockId, "Renz", null, "renzonifico@gmail.com", null);

        UpdateUserCommand actual = userMapper.toUpdateCommand(mockId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateCommand_hasNullRequestBody_shouldReturnNullUpdateUserCommand() {
        UpdateUserRequest request = new UpdateUserRequest(null, null, null, null);
        UpdateUserCommand expected = new UpdateUserCommand(mockId, null, null, null, null);

        UpdateUserCommand actual = userMapper.toUpdateCommand(mockId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasNoNullFields_shouldReturnUser() {
        UpdateUserCommand command = new UpdateUserCommand(mockId, "Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        User expected = new User(null, "Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword", null);

        User actual = userMapper.toUpdatedUser(new User(), command, "mockPassword");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasPartialNullFields_shouldReturnUser() {
        UpdateUserCommand command = new UpdateUserCommand(mockId, "Renz", null, "renzonifico@gmail.com", null);
        User expected = new User(null, "Renz", null, "renzonifico@gmail.com", null, null);

        User actual = userMapper.toUpdatedUser(new User(), command, "mockPassword");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasFullNullFields_shouldReturnUser() {
        UpdateUserCommand command = new UpdateUserCommand(mockId, null, null, null, null);
        User expected = new User(null, null, null, null, null, null);

        User actual = userMapper.toUpdatedUser(new User(), command, null);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.junit.jupiter.api.Assertions.*;

public class UserMapperUnitTest {
    private final UserMapper userMapper = new UserMapper();
    private final UUID mockId1 = UUID.fromString("11111111-1111-1111-111-111111111111");
    private final User mockUser = new User(mockId1, "Renz","Tabuzo","renzonifico@gmail.com",null, null);
    private final UserResponse mockUserResponse = new UserResponse(mockId1, "Renz", "Tabuzo", "renzonifico@gmail.com");

    @Test
    void toResponse_validUserObject_shouldReturnUserResponse() {
        UserResponse result = userMapper.toResponse(mockUser);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(mockUserResponse);
    }

    @Test
    void toResponse_nullUserObject_shouldReturnUserResponse() {
        UserResponse result = userMapper.toResponse(new User());

        assertAll("Check UserResponse shape",
                () -> assertNull(result.id()),
                () -> assertNull(result.firstName()),
                () -> assertNull(result.lastName()),
                () -> assertNull(result.email())
        );
    }

    @Test
    void toCreateCommand_validRequestBody_shouldReturnCreateUserCommand() {
        CreateUserRequest mockRequest = new CreateUserRequest("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        CreateUserCommand mockCommand = userMapper.toCreateCommand(mockRequest);

        assertAll("Check CreateCommand shape",
                () -> assertEquals("Renz", mockCommand.firstName()),
                () -> assertEquals("Tabuzo", mockCommand.lastName()),
                () -> assertEquals("renzonifico@gmail.com", mockCommand.email()),
                () -> assertEquals("mockPassword", mockCommand.password())
        );
    }

    @Test
    void toUserFromCreateCommand_validCommandBody_shouldReturnUser() {
        CreateUserCommand mockCommand = new CreateUserCommand("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        User mockUser = userMapper.toNewUser(mockCommand, "mockPassword");

        assertThat(mockUser.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertAll("Check User shape",
                () -> assertEquals("Renz", mockUser.getFirstName()),
                () -> assertEquals("Tabuzo", mockUser.getLastName()),
                () -> assertEquals("renzonifico@gmail.com", mockUser.getEmail()),
                () -> assertEquals("mockPassword", mockUser.getPasswordHash())
        );
    }

    @Test
    void toUserFromCreateCommand_nullCommandBody_shouldReturnUser() {
        CreateUserCommand mockCommand = new CreateUserCommand(null, null, null, null);
        User mockUser = userMapper.toNewUser(mockCommand, null);

        assertAll("Check User shape",
                () -> assertNull(mockUser.getFirstName()),
                () -> assertNull(mockUser.getLastName()),
                () -> assertNull(mockUser.getEmail()),
                () -> assertNull(mockUser.getPasswordHash())
        );
    }

    @Test
    void toCreateCommand_nullRequestBody_shouldReturnNullCreateUserCommand() {
        CreateUserRequest mockRequest = new CreateUserRequest(null, null, null, null);
        CreateUserCommand mockCommand = userMapper.toCreateCommand(mockRequest);

        assertAll("Check CreateCommand shape",
                () -> assertNull(mockCommand.firstName()),
                () -> assertNull(mockCommand.lastName()),
                () -> assertNull(mockCommand.email()),
                () -> assertNull(mockCommand.password())
        );
    }

    @Test
    void toUpdateCommand_requestBodyHasNoNullFields_shouldReturnUpdateUserCommand() {
        UpdateUserRequest mockUpdateRequest = new UpdateUserRequest("Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        UpdateUserCommand mockCommand = userMapper.toUpdateCommand(mockId1, mockUpdateRequest);

        assertAll("Check UpdateCommand shape",
                () -> assertEquals("Renz", mockCommand.firstName()),
                () -> assertEquals("Tabuzo", mockCommand.lastName()),
                () -> assertEquals("renzonifico@gmail.com", mockCommand.email()),
                () -> assertEquals("mockPassword", mockCommand.password())
        );
    }

    @Test
    void toUpdateCommand_hasPartiallyNullRequestBody_shouldReturnUpdateUserCommand() {
        UpdateUserRequest mockUpdateRequest = new UpdateUserRequest("Renz", null, "renzonifico@gmail.com", null);
        UpdateUserCommand mockCommand = userMapper.toUpdateCommand(mockId1, mockUpdateRequest);

        assertAll("Check UpdateCommand shape",
                () -> assertEquals("Renz", mockCommand.firstName()),
                () -> assertNull(mockCommand.lastName()),
                () -> assertEquals("renzonifico@gmail.com", mockCommand.email()),
                () -> assertNull(mockCommand.password())
        );
    }

    @Test
    void toUpdateCommand_hasNullRequestBody_shouldReturnNullUpdateUserCommand() {
        UpdateUserRequest mockUpdateRequest = new UpdateUserRequest(null, null, null, null);
        UpdateUserCommand mockCommand = userMapper.toUpdateCommand(mockId1, mockUpdateRequest);

        assertAll("Check UpdateCommand shape",
                () -> assertNull(mockCommand.firstName()),
                () -> assertNull(mockCommand.lastName()),
                () -> assertNull(mockCommand.email()),
                () -> assertNull(mockCommand.password())
        );
    }

    @Test
    void toUserFromUpdateCommand_commandHasNoNullFields_shouldReturnUser() {
        UpdateUserCommand mockCommand = new UpdateUserCommand(mockId1, "Renz", "Tabuzo", "renzonifico@gmail.com", "mockPassword");
        User mockUser = userMapper.toUpdatedUser(new User(), mockCommand, "mockPassword");

        assertAll("Check User shape",
                () -> assertEquals("Renz", mockUser.getFirstName()),
                () -> assertEquals("Tabuzo", mockUser.getLastName()),
                () -> assertEquals("renzonifico@gmail.com", mockUser.getEmail()),
                () -> assertEquals("mockPassword", mockUser.getPasswordHash())
        );
    }

    @Test
    void toUserFromUpdateCommand_commandHasPartialNullFields_shouldReturnUser() {
        UpdateUserCommand mockCommand = new UpdateUserCommand(mockId1, "Renz", null, "renzonifico@gmail.com", null);
        User mockUser = userMapper.toUpdatedUser(new User(), mockCommand, "mockPassword");

        assertAll("Check User shape",
                () -> assertEquals("Renz", mockUser.getFirstName()),
                () -> assertNull(mockUser.getLastName()),
                () -> assertEquals("renzonifico@gmail.com", mockUser.getEmail()),
                () -> assertNull(mockUser.getPasswordHash())
        );
    }

    @Test
    void toUserFromUpdateCommand_commandHasFullNullFields_shouldReturnUser() {
        UpdateUserCommand mockCommand = new UpdateUserCommand(mockId1, null, null, null, null);
        User mockUser = userMapper.toUpdatedUser(new User(), mockCommand, null);

        assertAll("Check User shape",
                () -> assertNull(mockUser.getFirstName()),
                () -> assertNull(mockUser.getLastName()),
                () -> assertNull(mockUser.getEmail()),
                () -> assertNull(mockUser.getPasswordHash())
        );
    }
}

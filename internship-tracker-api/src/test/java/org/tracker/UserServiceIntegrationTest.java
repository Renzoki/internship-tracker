package org.tracker;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tracker.exception.EmailAlreadyExistsException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.UserMapper;
import org.tracker.model.business.CreateUserCommand;
import org.tracker.model.business.UpdateUserCommand;
import org.tracker.model.entities.User;
import org.tracker.repository.UserRepository;
import org.tracker.service.UserService;
import org.tracker.service.impl.UserServiceImpl;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserServiceImpl.class, UserMapper.class, BCryptPasswordEncoder.class, ValidationAutoConfiguration.class})
public class UserServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(buildUser("Vincent", "Tabuzo", "renzonifico@gmail.com"));
        user2 = userRepository.save(buildUser("Nicole", "Ortega", "noortega@up.edu.ph"));
    }

    private User buildUser(String firstName, String lastName, String email) {
        return new User(
                firstName,
                lastName,
                email,
                passwordEncoder.encode("password1"),
                Instant.now()
        );
    }

    @Test
    public void getUserById_idExists_shouldReturnUser() {
        User result = userService.getUserById(user1.getId());

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(user1);
    }

    @Test
    public void getUserById_idDoesNotExist_shouldThrowUserNotFoundException() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        assertThatThrownBy(() -> userService.getUserById(nonExistentId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot find user with id '" + nonExistentId + "'.");

    }

    @Test
    public void createNewUser_validCreateCommand_shouldReturnUser() {
        CreateUserCommand command = new CreateUserCommand("John", "Smith", "johnSmith@gmail.com", "password123");
        User newUser = userService.createNewUser(command);

        User result = userRepository.findById(newUser.getId()).orElseThrow();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(newUser);

        assertThat(passwordEncoder.matches("password123", result.getPasswordHash())).isTrue();
    }

    @Test
    public void createNewUser_invalidCreateCommand_shouldThrowConstraintViolationException() {
        CreateUserCommand command = new CreateUserCommand(null, "Smith", "johnSmith@gmail.com", "password123");
        assertThatThrownBy(() -> userService.createNewUser(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("First name cannot be blank.");
    }

    @Test
    public void createNewUser_emailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        CreateUserCommand command = new CreateUserCommand("John", "Smith", "renzonifico@gmail.com", "password123");
        assertThatThrownBy(() -> userService.createNewUser(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email address 'renzonifico@gmail.com' is already being used.");
    }

    @Test
    public void updateExistingUser_validUpdateCommand_shouldReturnUser() {
        UpdateUserCommand command = new UpdateUserCommand(user1.getId(), "Jepoy", "Dizon", "jepoyDizon@gmail.com", "12345678910");
        User updatedUser = userService.updateExistingUser(command);
        User result = userRepository.findById(user1.getId()).orElseThrow();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void updateExistingUser_containsBlankFields_shouldThrowConstraintViolationException() {
        UpdateUserCommand command = new UpdateUserCommand(user1.getId(), "Renz", " ", "vincent@gmail.com", "password123");

        assertThatThrownBy(() -> userService.updateExistingUser(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Last name cannot be blank.");
    }

    @Test
    public void updateExistingUser_allFieldsNull_shouldThrowConstraintViolationException() {
        UpdateUserCommand command = new UpdateUserCommand(user1.getId(), null, null, null, null);

        assertThatThrownBy(() -> userService.updateExistingUser(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("At least one field must be provided for update.");
    }

    @Test
    public void updateExistingUser_invalidEmailProvided_shouldThrowConstraintViolationException() {
        UpdateUserCommand command = new UpdateUserCommand(user1.getId(), "Renz", "Tabuzo", "wowzers", "password123");

        assertThatThrownBy(() -> userService.updateExistingUser(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Invalid email provided.");
    }

    @Test
    public void updateExistingUser_userDoesNotExist_shouldThrowUserNotFoundException() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateUserCommand command = new UpdateUserCommand(nonExistentId, "Renz", "Tabuzo", user2.getEmail(), "password123");

        assertThatThrownBy(() -> userService.updateExistingUser(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot find user with id '" + nonExistentId + "'.");
    }

    @Test
    public void updateExistingUser_emailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        UpdateUserCommand command = new UpdateUserCommand(user1.getId(), "Renz", "Tabuzo", "noortega@up.edu.ph", "password123");

        assertThatThrownBy(() -> userService.updateExistingUser(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email address 'noortega@up.edu.ph' is already being used.");
    }

    @Test
    public void deleteUserById_userExists_shouldReturnNoContent() {
        userService.deleteUserById(user1.getId());
        assertThat(userRepository.findById(user1.getId())).isEmpty();
    }

    @Test
    public void deleteUserById_userDoesNotExist_shouldThrowUserNotFoundException() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-11111111111");

        assertThatThrownBy(() -> userService.deleteUserById(nonExistentId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot find user with id '" + nonExistentId + "'.");
    }
}
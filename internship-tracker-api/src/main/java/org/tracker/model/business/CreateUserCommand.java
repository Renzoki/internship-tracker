package org.tracker.model.business;

public record CreateUserCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}

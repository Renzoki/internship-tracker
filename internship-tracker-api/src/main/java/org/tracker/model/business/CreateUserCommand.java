package org.tracker.model.business;

public record CreateUserCommand( //TODO: Add AuthenticationPrincipal values here
        String firstName,
        String lastName,
        String email,
        String password
) {
}

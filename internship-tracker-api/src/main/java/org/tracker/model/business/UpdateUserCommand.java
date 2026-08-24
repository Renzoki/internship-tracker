package org.tracker.model.business;

import java.util.UUID;

public record UpdateUserCommand(
        //TODO: Add AuthenticationPrincipal values here
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String password
) {
}
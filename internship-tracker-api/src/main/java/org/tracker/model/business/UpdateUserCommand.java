package org.tracker.model.business;

import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String password
) {
}
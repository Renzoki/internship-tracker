package org.tracker.exception;

import java.util.UUID;

public class ApplicationAccessDeniedException extends RuntimeException {
    public ApplicationAccessDeniedException(UUID userId, UUID applicationId) {
        super("User with id '" + userId + "' does not own application with id '" + applicationId + "'.");
    }
}

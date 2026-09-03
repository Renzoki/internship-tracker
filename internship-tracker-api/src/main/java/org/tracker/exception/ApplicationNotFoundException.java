package org.tracker.exception;

import java.util.UUID;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(UUID applicationId) {
        super("Cannot find application with id '" + applicationId + "'.");
    }
}

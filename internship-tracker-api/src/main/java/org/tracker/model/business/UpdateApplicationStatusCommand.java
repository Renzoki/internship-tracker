package org.tracker.model.business;

import jakarta.validation.constraints.NotNull;
import org.tracker.model.enums.ApplicationStatus;

import java.util.UUID;

public record UpdateApplicationStatusCommand(
        @NotNull(message = NULL_USER_ID)
        UUID userId,

        @NotNull(message = NULL_APPLICATION_ID)
        UUID applicationId,

        @NotNull(message = NULL_APPLICATION_STATUS)
        ApplicationStatus status
) {
    private static final String NULL_USER_ID = "User id cannot be null.";
    private static final String NULL_APPLICATION_ID = "Application id cannot be null.";
    private static final String NULL_APPLICATION_STATUS = "Application status cannot be null.";
}

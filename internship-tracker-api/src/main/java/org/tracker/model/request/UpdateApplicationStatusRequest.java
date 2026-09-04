package org.tracker.model.request;

import jakarta.validation.constraints.NotNull;
import org.tracker.model.enums.ApplicationStatus;

public record UpdateApplicationStatusRequest(
        @NotNull(message = NULL_STATUS_ERROR)
        ApplicationStatus status
) {
    private final static String NULL_STATUS_ERROR = "Status cannot be null.";
}

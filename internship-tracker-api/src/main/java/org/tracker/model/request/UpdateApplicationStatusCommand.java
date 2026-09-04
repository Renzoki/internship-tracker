package org.tracker.model.request;

import org.tracker.model.enums.ApplicationStatus;

import java.util.UUID;

public record UpdateApplicationStatusCommand(
        UUID userId,
        UUID applicationId,
        ApplicationStatus status
) {
}

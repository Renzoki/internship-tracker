package org.tracker.model.business;

import org.tracker.model.enums.WorkMode;

import java.util.UUID;

public record UpdateApplicationDetailsCommand(
        UUID userId,
        UUID applicationId,
        String companyName,
        String positionTitle,
        String location,
        WorkMode workMode,
        String applicationUrl
) {
}

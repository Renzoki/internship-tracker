package org.tracker.model.response;

import org.tracker.model.enums.ApplicationStatus;
import org.tracker.model.enums.WorkMode;

import java.time.LocalDate;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        String companyName,
        String positionTitle,
        String location,
        WorkMode workMode,
        String applicationUrl,
        ApplicationStatus status,
        LocalDate dateApplied
) {
}

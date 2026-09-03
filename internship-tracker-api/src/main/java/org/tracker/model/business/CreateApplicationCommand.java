package org.tracker.model.business;

import org.tracker.model.enums.WorkMode;

import java.time.LocalDate;
import java.util.UUID;

public record CreateApplicationCommand(
        UUID userId,
        String companyName,
        String positionTitle,
        String location,
        WorkMode workMode,
        String applicationUrl,
        LocalDate dateApplied
) {

}

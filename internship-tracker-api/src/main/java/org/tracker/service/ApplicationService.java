package org.tracker.service;

import jakarta.validation.Valid;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.business.UpdateApplicationStatusCommand;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    List<Application> getAllApplications(UUID userId);
    Application getApplicationById(UUID applicationId, UUID userId);
    Application addNewApplication(@Valid CreateApplicationCommand command);
    Application updateApplicationDetails(@Valid UpdateApplicationDetailsCommand command);
    Application updateApplicationStatus(@Valid UpdateApplicationStatusCommand command);
    void deleteApplicationById(UUID applicationId, UUID userId);
}

package org.tracker.service;

import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.entities.Application;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    List<Application> getAllApplications(UUID userId);
    Application getApplicationById(UUID applicationId, UUID userId);
    Application addNewApplication(CreateApplicationCommand command);
}

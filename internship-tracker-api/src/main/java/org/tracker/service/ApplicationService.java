package org.tracker.service;

import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.request.UpdateApplicationDetailsRequest;
import org.tracker.model.request.UpdateApplicationStatusCommand;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    List<Application> getAllApplications(UUID userId);
    Application getApplicationById(UUID applicationId, UUID userId);
    Application addNewApplication(CreateApplicationCommand command);
    Application updateApplicationDetails(UpdateApplicationDetailsCommand command);
    Application updateApplicationStatus(UpdateApplicationStatusCommand command);
    void deleteApplicationById(UUID applicationId, UUID userId);
}

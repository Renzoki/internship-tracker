package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.request.CreateApplicationRequest;
import org.tracker.model.response.ApplicationResponse;

import java.util.UUID;

@Component
public class ApplicationMapper {
    public ApplicationResponse toResponse(Application application){
        return new ApplicationResponse(
                application.getId(),
                application.getCompanyName(),
                application.getPositionTitle(),
                application.getLocation(),
                application.getWorkMode(),
                application.getApplicationUrl(),
                application.getStatus(),
                application.getDateApplied()
        );
    }

    public CreateApplicationCommand toCreateCommand(UUID userId, CreateApplicationRequest request){
        return new CreateApplicationCommand(
                userId,
                request.companyName(),
                request.positionTitle(),
                request.location(),
                request.workMode(),
                request.applicationUrl(),
                request.dateApplied());
    }
}

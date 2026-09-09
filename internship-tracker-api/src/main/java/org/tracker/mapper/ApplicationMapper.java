package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.model.request.CreateApplicationRequest;
import org.tracker.model.request.UpdateApplicationDetailsRequest;
import org.tracker.model.business.UpdateApplicationStatusCommand;
import org.tracker.model.request.UpdateApplicationStatusRequest;
import org.tracker.model.response.ApplicationResponse;

import java.time.Instant;
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

    public Application toNewApplication(User user, CreateApplicationCommand command){
        return new Application(
                user,
                command.companyName(),
                command.positionTitle(),
                command.location(),
                command.workMode(),
                command.applicationUrl(),
                command.dateApplied(),
                Instant.now());
    }

    public UpdateApplicationDetailsCommand toUpdateDetailsCommand(UUID userId, UUID applicationId, UpdateApplicationDetailsRequest request){
        return new UpdateApplicationDetailsCommand(
                userId,
                applicationId,
                request.companyName(),
                request.positionTitle(),
                request.location(),
                request.workMode(),
                request.applicationUrl()
        );
    }

    public UpdateApplicationStatusCommand toUpdateStatusCommand(UUID userId, UUID applicationId, UpdateApplicationStatusRequest request){
        return new UpdateApplicationStatusCommand(
                userId,
                applicationId,
                request.status()
        );
    }

    public Application toUpdatedApplication(Application application, UpdateApplicationDetailsCommand command){
        if(command.companyName() != null){
            application.setCompanyName(command.companyName());
        }

        if(command.positionTitle() != null){
            application.setPositionTitle(command.positionTitle());
        }

        if(command.location() != null){
            application.setLocation(command.location());
        }

        if(command.workMode() != null){
            application.setWorkMode(command.workMode());
        }

        if(command.applicationUrl() != null){
            application.setApplicationUrl(command.applicationUrl());
        }

        return application;
    }
}

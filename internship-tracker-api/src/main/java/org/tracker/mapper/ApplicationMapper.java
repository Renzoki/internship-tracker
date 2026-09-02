package org.tracker.mapper;

import org.springframework.stereotype.Component;
import org.tracker.model.entities.Application;
import org.tracker.model.response.ApplicationResponse;

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
}

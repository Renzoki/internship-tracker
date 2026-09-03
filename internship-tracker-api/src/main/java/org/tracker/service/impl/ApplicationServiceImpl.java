package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.exception.ApplicationAccessDeniedException;
import org.tracker.exception.ApplicationNotFoundException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.repository.ApplicationRepository;
import org.tracker.repository.UserRepository;
import org.tracker.service.ApplicationService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository){
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Application> getAllApplications(UUID userId) {
        return applicationRepository.findAllByUserId(userId);
    }

    @Override
    public Application getApplicationById(UUID applicationId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return applicationRepository.findByIdAndUser(applicationId, user)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
    }

    @Override
    public Application addNewApplication(CreateApplicationCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        Application application = new Application(
                user,
                command.companyName(),
                command.positionTitle(),
                command.location(),
                command.workMode(),
                command.applicationUrl(),
                command.dateApplied(),
                Instant.now());

        user.addApplication(application);
        return applicationRepository.save(application);
    }

    @Override
    public Application updateApplicationDetails(UpdateApplicationDetailsCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        Application application = applicationRepository.findById(command.applicationId())
                .orElseThrow(() -> new ApplicationNotFoundException(command.applicationId()));

        if(!user.getApplicationList().contains(application)){
            throw new ApplicationAccessDeniedException(command.userId(), command.applicationId());
        }

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

        return applicationRepository.save(application);
    }
}

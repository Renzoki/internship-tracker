package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.tracker.exception.ApplicationAccessDeniedException;
import org.tracker.exception.ApplicationNotFoundException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.model.business.UpdateApplicationStatusCommand;
import org.tracker.repository.ApplicationRepository;
import org.tracker.repository.UserRepository;
import org.tracker.service.ApplicationService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper mapper;
    private final UserRepository userRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository, ApplicationMapper mapper){
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Application> getAllApplications(UUID userId) {
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return applicationRepository.findAllByUserId(userId);
    }

    @Override
    public Application getApplicationById(UUID applicationId, UUID userId) {
        User user = getUser(userId);
        Application application = getApplication(applicationId);

        if(!user.getApplicationList().contains(application)){
            throw new ApplicationAccessDeniedException(userId, applicationId);
        }

        return application;
    }

    @Override
    public Application addNewApplication(CreateApplicationCommand command) {
        User user = getUser(command.userId());
        Application application = mapper.toNewApplication(user, command);

        user.addApplication(application);
        return applicationRepository.save(application);
    }

    @Override
    public Application updateApplicationDetails(UpdateApplicationDetailsCommand command) {
        User user = getUser(command.userId());
        Application application = getApplication(command.applicationId());

        if(!user.getApplicationList().contains(application)){
            throw new ApplicationAccessDeniedException(command.userId(), command.applicationId());
        }

        application = mapper.toUpdatedApplication(application, command);
        application.setUpdatedAt(Instant.now());
        return applicationRepository.save(application);
    }

    @Override
    public Application updateApplicationStatus(UpdateApplicationStatusCommand command) {
        User user = getUser(command.userId());
        Application application = getApplication(command.applicationId());

        if(!user.getApplicationList().contains(application)){
            throw new ApplicationAccessDeniedException(command.userId(), command.applicationId());
        }

        application.setUpdatedAt(Instant.now());
        application.setStatus(command.status());
        return applicationRepository.save(application);
    }

    @Override
    public void deleteApplicationById(UUID applicationId, UUID userId) {
        User user = getUser(userId);
        Application application = getApplication(applicationId);

        if(!user.getApplicationList().contains(application)){
            throw new ApplicationAccessDeniedException(userId, applicationId);
        }

        applicationRepository.delete(application);
    }

    private User getUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

    }

    private Application getApplication(UUID applicationId){
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
    }
}

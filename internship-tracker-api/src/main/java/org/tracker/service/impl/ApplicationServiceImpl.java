package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.exception.ApplicationNotFoundException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.model.business.CreateApplicationCommand;
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
}

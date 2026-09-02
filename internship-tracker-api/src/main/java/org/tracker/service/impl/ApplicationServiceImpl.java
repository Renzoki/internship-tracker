package org.tracker.service.impl;

import org.springframework.stereotype.Service;
import org.tracker.model.entities.Application;
import org.tracker.repository.ApplicationRepository;
import org.tracker.service.ApplicationService;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository){
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<Application> getAllApplications(UUID userId) {
        return applicationRepository.findAllByUserId(userId);
    }
}

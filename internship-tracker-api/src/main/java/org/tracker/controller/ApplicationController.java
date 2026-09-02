package org.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tracker.configuration.UserPrincipal;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.entities.Application;
import org.tracker.model.response.ApplicationResponse;
import org.tracker.service.ApplicationService;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    private final ApplicationMapper mapper;

    public ApplicationController(ApplicationService applicationService, ApplicationMapper mapper){
        this.applicationService = applicationService;
        this.mapper = mapper;
    }

    @GetMapping("/self")
    public ResponseEntity<List<ApplicationResponse>> getAllApplicationsByUserId(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        List<Application> applicationList = applicationService.getAllApplications(principal.id());
        List<ApplicationResponse> response = applicationList.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

}

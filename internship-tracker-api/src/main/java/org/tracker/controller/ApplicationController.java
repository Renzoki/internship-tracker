package org.tracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.tracker.configuration.UserPrincipal;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.request.CreateApplicationRequest;
import org.tracker.model.response.ApplicationResponse;
import org.tracker.service.ApplicationService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<ApplicationResponse> addNewApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateApplicationRequest request
    ){
        CreateApplicationCommand command = mapper.toCreateCommand(principal.id(), request);
        Application application = applicationService.addNewApplication(command);
        ApplicationResponse response = mapper.toResponse(application);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/self/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

}

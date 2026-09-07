package org.tracker;

import org.junit.jupiter.api.Test;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.model.enums.ApplicationStatus;
import org.tracker.model.enums.WorkMode;
import org.tracker.model.request.CreateApplicationRequest;
import org.tracker.model.request.UpdateApplicationDetailsRequest;
import org.tracker.model.request.UpdateApplicationStatusCommand;
import org.tracker.model.request.UpdateApplicationStatusRequest;
import org.tracker.model.response.ApplicationResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ApplicationMapperUnitTest {

    private final ApplicationMapper applicationMapper = new ApplicationMapper();
    private final UUID mockUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID mockAppId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final User mockUser = new User();
    private final LocalDate mockDate = LocalDate.of(2026, 1, 15);

    @Test
    void toResponse_validApplicationObject_shouldReturnApplicationResponse() {
        Application mockApplication = new Application(
                mockAppId, mockUser, "Google", "Software Engineer",
                "Mountain View, CA", WorkMode.HYBRID, "https://careers.google.com/jobs/123",
                mockDate, Instant.now()
        );

        ApplicationResponse expected = new ApplicationResponse(
                mockAppId, "Google", "Software Engineer",
                "Mountain View, CA", WorkMode.HYBRID, "https://careers.google.com/jobs/123",
                ApplicationStatus.APPLIED, mockDate
        );

        ApplicationResponse actual = applicationMapper.toResponse(mockApplication);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse_nullFieldsApplicationObject_shouldReturnApplicationResponse() {
        Application mockApplication = new Application();
        ApplicationResponse expected = new ApplicationResponse(null, null, null, null, null, null, null, null);

        ApplicationResponse actual = applicationMapper.toResponse(mockApplication);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toCreateCommand_validRequestBody_shouldReturnCreateApplicationCommand() {
        CreateApplicationRequest request = new CreateApplicationRequest(
                "Google", "Software Engineer", "Mountain View, CA",
                WorkMode.HYBRID, "https://careers.google.com/jobs/123", mockDate
        );

        CreateApplicationCommand expected = new CreateApplicationCommand(
                mockUserId, "Google", "Software Engineer", "Mountain View, CA",
                WorkMode.HYBRID, "https://careers.google.com/jobs/123", mockDate
        );

        CreateApplicationCommand actual = applicationMapper.toCreateCommand(mockUserId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toCreateCommand_nullRequestBody_shouldReturnCreateApplicationCommand() {
        CreateApplicationRequest request = new CreateApplicationRequest(null, null, null, null, null, null);
        CreateApplicationCommand expected = new CreateApplicationCommand(mockUserId, null, null, null, null, null, null);

        CreateApplicationCommand actual = applicationMapper.toCreateCommand(mockUserId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toNewApplication_validCommandBody_shouldReturnApplication() {
        CreateApplicationCommand command = new CreateApplicationCommand(
                mockUserId, "Google", "Software Engineer", "Mountain View, CA",
                WorkMode.HYBRID, "https://careers.google.com/jobs/123", mockDate
        );

        Application expected = new Application(
                mockUser, "Google", "Software Engineer", "Mountain View, CA",
                WorkMode.HYBRID, "https://careers.google.com/jobs/123", mockDate, null
        );

        Application actual = applicationMapper.toNewApplication(mockUser, command);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);

        assertThat(actual.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertThat(actual.getUpdatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void toNewApplication_nullCommandBody_shouldReturnApplication() {
        CreateApplicationCommand command = new CreateApplicationCommand(mockUserId, null, null, null, null, null, null);
        Application expected = new Application(mockUser, null, null, null, null, null, null, null);

        Application actual = applicationMapper.toNewApplication(mockUser, command);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toUpdateDetailsCommand_requestBodyHasNoNullFields_shouldReturnUpdateApplicationDetailsCommand() {
        UpdateApplicationDetailsRequest request = new UpdateApplicationDetailsRequest(
                "Google", "Senior Software Engineer", "Remote",
                WorkMode.REMOTE, "https://careers.google.com/jobs/456"
        );

        UpdateApplicationDetailsCommand expected = new UpdateApplicationDetailsCommand(
                mockUserId, mockAppId, "Google", "Senior Software Engineer",
                "Remote", WorkMode.REMOTE, "https://careers.google.com/jobs/456"
        );

        UpdateApplicationDetailsCommand actual = applicationMapper.toUpdateDetailsCommand(mockUserId, mockAppId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateDetailsCommand_hasPartiallyNullRequestBody_shouldReturnUpdateApplicationDetailsCommand() {
        UpdateApplicationDetailsRequest request = new UpdateApplicationDetailsRequest(
                "Google", null, "Remote", null, null
        );

        UpdateApplicationDetailsCommand expected = new UpdateApplicationDetailsCommand(
                mockUserId, mockAppId, "Google", null, "Remote", null, null
        );

        UpdateApplicationDetailsCommand actual = applicationMapper.toUpdateDetailsCommand(mockUserId, mockAppId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateDetailsCommand_hasNullRequestBody_shouldReturnUpdateApplicationDetailsCommand() {
        UpdateApplicationDetailsRequest request = new UpdateApplicationDetailsRequest(null, null, null, null, null);
        UpdateApplicationDetailsCommand expected = new UpdateApplicationDetailsCommand(mockUserId, mockAppId, null, null, null, null, null);

        UpdateApplicationDetailsCommand actual = applicationMapper.toUpdateDetailsCommand(mockUserId, mockAppId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateStatusCommand_validRequestBody_shouldReturnUpdateApplicationStatusCommand() {
        UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest(ApplicationStatus.FOR_INTERVIEW);
        UpdateApplicationStatusCommand expected = new UpdateApplicationStatusCommand(mockUserId, mockAppId, ApplicationStatus.FOR_INTERVIEW);

        UpdateApplicationStatusCommand actual = applicationMapper.toUpdateStatusCommand(mockUserId, mockAppId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUpdateStatusCommand_nullRequestBody_shouldReturnUpdateApplicationStatusCommand() {
        UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest(null);
        UpdateApplicationStatusCommand expected = new UpdateApplicationStatusCommand(mockUserId, mockAppId, null);

        UpdateApplicationStatusCommand actual = applicationMapper.toUpdateStatusCommand(mockUserId, mockAppId, request);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasNoNullFields_shouldReturnUpdatedApplication() {
        Application initialApp = new Application(
                mockAppId, mockUser, "Old Company", "Junior Dev",
                "On-Site", WorkMode.ONLINE, "https://old.com", mockDate, Instant.now()
        );

        UpdateApplicationDetailsCommand command = new UpdateApplicationDetailsCommand(
                mockUserId, mockAppId, "New Company", "Lead Dev",
                "Remote", WorkMode.REMOTE, "https://new.com"
        );

        Application expected = new Application(
                mockAppId, mockUser, "New Company", "Lead Dev",
                "Remote", WorkMode.REMOTE, "https://new.com", mockDate, Instant.now()
        );

        Application actual = applicationMapper.toUpdatedApplication(initialApp, command);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasPartialNullFields_shouldReturnUpdatedApplication() {
        Application initialApp = new Application(
                mockAppId, mockUser, "Old Company", "Junior Dev",
                "On-Site", WorkMode.ONLINE, "https://old.com", mockDate, Instant.now()
        );

        UpdateApplicationDetailsCommand command = new UpdateApplicationDetailsCommand(
                mockUserId, mockAppId, "New Company", null, null, WorkMode.REMOTE, null
        );

        Application expected = new Application(
                mockAppId, mockUser, "New Company", "Junior Dev",
                "On-Site", WorkMode.REMOTE, "https://old.com", mockDate, Instant.now()
        );

        Application actual = applicationMapper.toUpdatedApplication(initialApp, command);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toUserFromUpdateCommand_commandHasFullNullFields_shouldReturnUnmodifiedApplication() {
        Application initialApp = new Application(
                mockAppId, mockUser, "Old Company", "Junior Dev",
                "On-Site", WorkMode.ONLINE, "https://old.com", mockDate, Instant.now()
        );

        UpdateApplicationDetailsCommand command = new UpdateApplicationDetailsCommand(
                mockUserId, mockAppId, null, null, null, null, null
        );

        Application expected = new Application(
                mockAppId, mockUser, "Old Company", "Junior Dev",
                "On-Site", WorkMode.ONLINE, "https://old.com", mockDate, Instant.now()
        );

        Application actual = applicationMapper.toUpdatedApplication(initialApp, command);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }
}
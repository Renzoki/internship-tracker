package org.tracker;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tracker.exception.ApplicationAccessDeniedException;
import org.tracker.exception.ApplicationNotFoundException;
import org.tracker.exception.InvalidApplicationStatusAssignmentException;
import org.tracker.exception.UserNotFoundException;
import org.tracker.mapper.ApplicationMapper;
import org.tracker.model.business.CreateApplicationCommand;
import org.tracker.model.business.UpdateApplicationDetailsCommand;
import org.tracker.model.business.UpdateApplicationStatusCommand;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.model.enums.ApplicationStatus;
import org.tracker.model.enums.WorkMode;
import org.tracker.repository.ApplicationRepository;
import org.tracker.repository.UserRepository;
import org.tracker.service.ApplicationService;
import org.tracker.service.impl.ApplicationServiceImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ApplicationServiceImpl.class,
         ApplicationMapper.class,
         BCryptPasswordEncoder.class,
         ValidationAutoConfiguration.class,
         TestEntityManager.class
})
public class ApplicationServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private Application application1;
    private Application application2;
    private Application application3;
    private Application application4;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(buildUser("Vincent", "Tabuzo", "renzonifico@gmail.com"));
        user2 = userRepository.save(buildUser("Nicole", "Ortega", "noortega@up.edu.ph"));
        application1 = applicationRepository.save(buildApplication(user1, "Oracle", "Software Engineer Intern",
                "Makati", WorkMode.HYBRID, "https://oracle.com", LocalDate.parse("2026-07-12")));
        application2 = applicationRepository.save(buildApplication(user1, "Microsoft", "QA Intern",
                "Bulacan", WorkMode.REMOTE, "https://microsoft.com", LocalDate.parse("2026-05-19")));
        application3 = applicationRepository.save(buildApplication(user2, "Google", "Backend Engineer Intern",
                "Taguig", WorkMode.ONSITE, "https://google.com", LocalDate.parse("2026-06-01")));
        application4 = applicationRepository.save(buildApplication(user2, "Amazon", "Cloud Engineer Intern",
                "Pasig", WorkMode.HYBRID, "https://amazon.com", LocalDate.parse("2026-08-15")));

        clearCache();
    }

    private User buildUser(String firstName, String lastName, String email) {
        return new User(
                firstName,
                lastName,
                email,
                passwordEncoder.encode("password1"),
                Instant.now()
        );
    }

    private Application buildApplication(User user, String companyName, String positionTitle, String location,
                                           WorkMode workMode, String applicationUrl, LocalDate dateApplied){
        return new Application(
                user,
                companyName,
                positionTitle,
                location,
                workMode,
                applicationUrl,
                dateApplied,
                Instant.now()
        );
    }

    @Test
    public void getAllApplications_userHasApplications_shouldReturnListOfApplications() {
        List<Application> applications = applicationService.getAllApplications(user1.getId());
            assertThat(applications)
                    .usingRecursiveComparison()
                    .ignoringFields("user", "createdAt", "updatedAt")
                    .isEqualTo(List.of(application1, application2));
    }

    @Test
    public void getAllApplications_userHasNoApplications_shouldReturnEmptyList() {
        User user3 = userRepository.save(buildUser("Michael", "Scott", "michaelScott@gmail.com"));

        clearCache();

        List<Application> applications = applicationService.getAllApplications(user3.getId());
        assertThat(applications).isEmpty();
    }

    @Test
    public void getAllApplications_invalidUserId_shouldThrowUserNotFoundException() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        assertThatThrownBy(() -> applicationService.getAllApplications(nonExistentId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot find user with id '" + nonExistentId + "'.");
    }

    @Test
    public void getApplicationById_applicationExists_shouldReturnApplication() {
        Application result = applicationService.getApplicationById(application1.getId(), user1.getId());

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("user", "createdAt", "updatedAt")
                .isEqualTo(application1);
    }

    @Test
    public void getApplicationById_userDoesNotExist_shouldThrowUserNotFoundException() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        assertThatThrownBy(() -> applicationService.getApplicationById(application1.getId(), nonExistentId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot find user with id '" + nonExistentId + "'.");
    }

    @Test
    public void getApplicationById_applicationDoesNotExist_shouldThrowApplicationNotFound() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        assertThatThrownBy(() -> applicationService.getApplicationById(nonExistentId, user1.getId()))
                .isInstanceOf(ApplicationNotFoundException.class)
                .hasMessageContaining("Cannot find application with id '" + nonExistentId + "'.");
    }

    @Test
    public void getApplicationById_userDoesNotOwnApplication_shouldThrowApplicationAccessDeniedException() {
        assertThatThrownBy(() -> applicationService.getApplicationById(application3.getId(), user1.getId()))
                .isInstanceOf(ApplicationAccessDeniedException.class)
                .hasMessageContaining("User with id '" + user1.getId() + "' does not own application with id '" + application3.getId() + "'.");
    }

    @Test
    public void addNewApplication_validCreateCommand_shouldReturnApplication() {
        CreateApplicationCommand command = new CreateApplicationCommand(user1.getId(), "Apple", "UI/UX Intern",
                "Pasay", WorkMode.ONSITE, "https://www.linkedin.com",  LocalDate.parse("2026-08-15"));

        Application application = applicationService.addNewApplication(command);
        clearCache();
        Application result = applicationRepository.findById(application.getId()).orElseThrow();

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("user", "createdAt", "updatedAt")
                .isEqualTo(application);
    }

    @Test
    public void addNewApplication_invalidUrl_shouldThrowConstraintViolationException() {
        CreateApplicationCommand command = new CreateApplicationCommand(user1.getId(), "Apple", "UI/UX Intern",
                "Pasay", WorkMode.ONSITE, "jonathan",  LocalDate.parse("2026-08-15"));

        assertThatThrownBy(() -> applicationService.addNewApplication(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Application URL must follow valid URL syntax.");
    }

    @Test
    public void updateApplicationDetails_validUpdateDetailsCommand_shouldReturnApplication() {
        UpdateApplicationDetailsCommand command = new UpdateApplicationDetailsCommand(user1.getId(), application1.getId(),
                "Pay Maya Inc.", application1.getPositionTitle(), application1.getLocation(),
                application1.getWorkMode(), application1.getApplicationUrl());

        Application application = applicationService.updateApplicationDetails(command);
        clearCache();
        Application result = applicationRepository.findById(application1.getId()).orElseThrow();

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("user", "createdAt", "updatedAt")
                .isEqualTo(application);

        assertThat(result.getCompanyName()).isEqualTo("Pay Maya Inc.");
        assertThat(result.getUpdatedAt()).isNotEqualTo(result.getCreatedAt());
    }

    @Test
    public void updateApplicationDetails_allFieldsAreNull_shouldThrowConstraintViolationException() {
        UpdateApplicationDetailsCommand command = new UpdateApplicationDetailsCommand(user1.getId(), application1.getId(),
                null, null, null, null, null);

        assertThatThrownBy(() -> applicationService.updateApplicationDetails(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("At least one field must be provided for update");
    }

    @Test
    public void updateApplicationStatus_validUpdateStatusCommand_shouldReturnApplication() {
        UpdateApplicationStatusCommand command = new UpdateApplicationStatusCommand(user1.getId(), application1.getId(), ApplicationStatus.FOR_INTERVIEW);

        Application application = applicationService.updateApplicationStatus(command);
        clearCache();
        Application result = applicationRepository.findById(application1.getId()).orElseThrow();

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("user", "createdAt", "updatedAt")
                .isEqualTo(application);

        assertThat(result.getUpdatedAt()).isNotEqualTo(result.getCreatedAt());
    }

    @Test
    public void updateApplicationStatus_nullApplicationStatus_shouldThrowConstraintViolationException() {
        UpdateApplicationStatusCommand command = new UpdateApplicationStatusCommand(user1.getId(), application1.getId(), null);

        assertThatThrownBy(() -> applicationService.updateApplicationStatus(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Application status cannot be null");
    }

    @Test
    public void updateApplicationStatus_invalidApplicationStatusProgression_shouldThrowInvalidApplicationStatusAssignmentException() {
        UpdateApplicationStatusCommand command = new UpdateApplicationStatusCommand(user1.getId(), application1.getId(), ApplicationStatus.INTERVIEW_COMPLETED);

        assertThatThrownBy(() -> applicationService.updateApplicationStatus(command))
                .isInstanceOf(InvalidApplicationStatusAssignmentException.class)
                .hasMessageContaining("Cannot go from '" + application1.getStatus() + "' status to '" + ApplicationStatus.INTERVIEW_COMPLETED + "' status.");
    }

    @Test
    public void deleteApplicationById_validApplicationAndUserId_shouldReturnNoContent() {
        applicationService.deleteApplicationById(application1.getId(), user1.getId());

        assertThatThrownBy(() -> applicationService.getApplicationById(application1.getId(), user1.getId()))
                .isInstanceOf(ApplicationNotFoundException.class)
                .hasMessageContaining("Cannot find application with id '" + application1.getId() + "'.");
    }

    private void clearCache() {
        entityManager.flush();
        entityManager.clear();
    }
}

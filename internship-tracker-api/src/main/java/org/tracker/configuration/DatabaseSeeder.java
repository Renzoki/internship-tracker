package org.tracker.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;
import org.tracker.model.enums.WorkMode;
import org.tracker.repository.ApplicationRepository;
import org.tracker.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, ApplicationRepository applicationRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User renz = userRepository.save(buildUser("Renz", "Tabuzo", "renzonifico@gmail.com"));
        Application application1 = applicationRepository.save(buildApplication(renz, "Oracle", "Software Engineer Intern",
                "Makati", WorkMode.HYBRID, "https://oracle.com", LocalDate.parse("2026-07-12")));
        Application application2 = applicationRepository.save(buildApplication(renz, "Microsoft", "QA Intern",
                "Bulacan", WorkMode.REMOTE, "https://microsoft.com", LocalDate.parse("2026-05-19")));
        Application application3 = applicationRepository.save(buildApplication(renz, "Google", "Backend Engineer Intern",
                "Taguig", WorkMode.ONSITE, "https://google.com", LocalDate.parse("2026-06-01")));
        Application application4 = applicationRepository.save(buildApplication(renz, "Amazon", "Cloud Engineer Intern",
                "Pasig", WorkMode.HYBRID, "https://amazon.com", LocalDate.parse("2026-08-15")));
    }

    private User buildUser(String firstName, String lastName, String email) {
        return new User(
                firstName,
                lastName,
                email,
                passwordEncoder.encode("password123"),
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
}

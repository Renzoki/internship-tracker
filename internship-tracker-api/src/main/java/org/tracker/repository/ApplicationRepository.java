package org.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tracker.model.entities.Application;
import org.tracker.model.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findAllByUserId(UUID userId);
    Optional<Application> findByIdAndUser(UUID id, User user);
}

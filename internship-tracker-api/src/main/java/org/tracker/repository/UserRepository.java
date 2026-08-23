package org.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tracker.model.entities.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}

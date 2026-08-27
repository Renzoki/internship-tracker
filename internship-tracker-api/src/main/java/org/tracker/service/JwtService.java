package org.tracker.service;

import org.tracker.model.entities.User;

import java.util.UUID;

public interface JwtService {
    String createJwtToken(User user);
    UUID extractId(String token);
    String extractEmail(String email);
}

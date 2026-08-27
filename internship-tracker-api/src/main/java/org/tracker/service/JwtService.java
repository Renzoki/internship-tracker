package org.tracker.service;

import org.tracker.model.entities.User;

public interface JwtService {
    String createJwtToken(User user);
}

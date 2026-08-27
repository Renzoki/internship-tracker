package org.tracker.service;

import org.tracker.model.request.LoginRequest;

public interface AuthService {
    String handleLogin(LoginRequest request);
}

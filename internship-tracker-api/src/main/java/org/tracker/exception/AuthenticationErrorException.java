package org.tracker.exception;

public class AuthenticationErrorException extends RuntimeException {
    public AuthenticationErrorException() {
        super("Invalid email or password provided.");
    }
}

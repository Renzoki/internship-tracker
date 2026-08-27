package org.tracker.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email address '" + email + "'  is already being used.");
    }
}

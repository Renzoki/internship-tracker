package org.tracker.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(

        @Email(message = INVALID_EMAIL_ERROR)
        @NotBlank(message = BLANK_EMAIL_ERROR)
        @Length(min = 1, max = 120, message = EMAIL_LENGTH_ERROR)
        String email,

        @NotBlank(message = BLANK_PASSWORD_ERROR)
        @Size(min = 8, message = PASSWORD_LENGTH_ERROR)
        String password
) {
    private static final String BLANK_EMAIL_ERROR = "Email cannot be blank.";
    private static final String BLANK_PASSWORD_ERROR = "Password cannot be blank.";
    private static final String EMAIL_LENGTH_ERROR = "Email must be between 1 and 120 characters.";
    private static final String INVALID_EMAIL_ERROR = "Invalid email provided.";
    private static final String PASSWORD_LENGTH_ERROR = "Password must be at least 8 characters.";
}

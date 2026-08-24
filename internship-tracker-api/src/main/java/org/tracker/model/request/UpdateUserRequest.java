package org.tracker.model.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.tracker.annotation.NullOrNotBlank;

public record UpdateUserRequest(
        @NullOrNotBlank(message = BLANK_FIRST_NAME_ERROR)
        @Length(min = 1, max = 50, message = FIRST_NAME_LENGTH_ERROR)
        String firstName,

        @NullOrNotBlank(message = BLANK_LAST_NAME_ERROR)
        @Length(min = 1, max = 50, message = LAST_NAME_LENGTH_ERROR)
        String lastName,

        @Email(message = INVALID_EMAIL_ERROR)
        @NullOrNotBlank(message = BLANK_EMAIL_ERROR)
        @Length(min = 1, max = 120, message = EMAIL_LENGTH_ERROR)
        String email,

        @NullOrNotBlank(message = BLANK_PASSWORD_ERROR)
        @Size(min = 8, message = PASSWORD_LENGTH_ERROR)
        String password
) {
    private static final String BLANK_FIRST_NAME_ERROR = "First name cannot be blank.";
    private static final String BLANK_LAST_NAME_ERROR = "Last name cannot be blank.";
    private static final String BLANK_EMAIL_ERROR = "Email cannot be blank.";
    private static final String BLANK_PASSWORD_ERROR = "Password cannot be blank.";
    private static final String FIRST_NAME_LENGTH_ERROR = "First name must be between 1 and 50 characters.";
    private static final String LAST_NAME_LENGTH_ERROR = "Last name must be between 1 and 50 characters.";
    private static final String EMAIL_LENGTH_ERROR = "Email must be between 1 and 120 characters.";
    private static final String INVALID_EMAIL_ERROR = "Invalid email provided.";
    private static final String PASSWORD_LENGTH_ERROR = "Password must be at least 8 characters.";

    @AssertTrue(message = "At least one field must be provided for update")
    public boolean isAnyFieldNonNull() {
        return firstName != null || lastName != null || email != null;
    }
}

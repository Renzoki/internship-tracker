package org.tracker.model.request;

import jakarta.validation.constraints.AssertTrue;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.tracker.annotation.NullOrNotBlank;
import org.tracker.model.enums.WorkMode;

import java.util.UUID;

public record UpdateApplicationDetailsRequest(
        @NullOrNotBlank(message = BLANK_COMPANY_NAME_ERROR)
        @Length(min = 1, max = 100, message = COMPANY_NAME_LENGTH_ERROR)
        String companyName,

        @NullOrNotBlank(message = BLANK_POSITION_TITLE_ERROR)
        @Length(min = 1, max = 50, message = POSITION_TITLE_LENGTH_ERROR)
        String positionTitle,

        @NullOrNotBlank(message = BLANK_LOCATION_ERROR)
        @Length(max = 150, message = LOCATION_LENGTH_ERROR)
        String location,

        WorkMode workMode,

        @URL(message = INVALID_URL_ERROR)
        @NullOrNotBlank(message = BLANK_APPLICATION_URL_ERROR)
        String applicationUrl
) {
    private static final String BLANK_COMPANY_NAME_ERROR = "Company name cannot be blank.";
    private static final String BLANK_POSITION_TITLE_ERROR = "Position title cannot be blank.";
    private static final String BLANK_LOCATION_ERROR = "Location cannot be blank.";
    private static final String BLANK_APPLICATION_URL_ERROR = "Application URL cannot be blank.";
    private static final String COMPANY_NAME_LENGTH_ERROR = "Company name must be between 1 and 100 characters long.";
    private static final String POSITION_TITLE_LENGTH_ERROR = "Position title must be between 1 and 50 characters long.";
    private static final String LOCATION_LENGTH_ERROR = "Location must be less than 150 characters long.";
    private static final String INVALID_URL_ERROR = "Application URL must follow valid URL syntax.";

    @AssertTrue(message = "At least one field must be provided for update")
    public boolean isAnyFieldNonNull() {
        return companyName != null || positionTitle != null || location != null || workMode != null || applicationUrl != null;
    }
}

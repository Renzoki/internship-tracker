package org.tracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.tracker.model.enums.WorkMode;

import java.time.LocalDate;

public record CreateApplicationRequest(
        @NotBlank(message = BLANK_COMPANY_NAME_ERROR)
        @Length(min = 1, max = 100, message = COMPANY_NAME_LENGTH_ERROR)
        String companyName,

        @NotBlank(message = BLANK_POSITION_TITLE_ERROR)
        @Length(min = 1, max = 50, message = POSITION_TITLE_LENGTH_ERROR)
        String positionTitle,

        @NotBlank(message = BLANK_LOCATION_ERROR)
        @Length(max = 150, message = LOCATION_LENGTH_ERROR)
        String location,

        @NotNull(message = NULL_WORK_MODE_ERROR)
        WorkMode workMode,

        @URL(message = INVALID_URL_ERROR)
        @NotBlank(message = BLANK_APPLICATION_URL_ERROR)
        String applicationUrl,

        @NotNull(message = NULL_DATE_APPLIED_ERROR)
        @PastOrPresent(message = FUTURE_APPLICATION_ERROR)
        LocalDate dateApplied
) {
    private static final String BLANK_COMPANY_NAME_ERROR = "Company name cannot be blank.";
    private static final String BLANK_POSITION_TITLE_ERROR = "Position title cannot be blank.";
    private static final String BLANK_LOCATION_ERROR = "Location cannot be blank.";
    private static final String BLANK_APPLICATION_URL_ERROR = "Application URL cannot be blank.";
    private static final String NULL_WORK_MODE_ERROR = "workMode field cannot be null.";
    private static final String NULL_DATE_APPLIED_ERROR = "dateApplied field canot be null.";
    private static final String COMPANY_NAME_LENGTH_ERROR = "Company name must be between 1 and 100 characters long.";
    private static final String POSITION_TITLE_LENGTH_ERROR = "Position title must be between 1 and 50 characters long.";
    private static final String LOCATION_LENGTH_ERROR = "Location must be less than 150 characters long.";
    private static final String INVALID_URL_ERROR = "Application URL must follow valid URL syntax.";
    private static final String FUTURE_APPLICATION_ERROR = "Application date cannot be in the future.";
}

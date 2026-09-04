package org.tracker.exception;

import org.tracker.model.enums.ApplicationStatus;

public class InvalidApplicationStatusAssignmentException extends RuntimeException {
    public InvalidApplicationStatusAssignmentException(ApplicationStatus applicationStatus, ApplicationStatus updateStatus) {
        super("Cannot go from '" + applicationStatus + "' status to '" + updateStatus + "' status.");
    }
}

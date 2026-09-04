package org.tracker.model.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ApplicationStatus {
    APPLIED,

    FOR_INTERVIEW,
    FOR_ASSESSMENT,
    INTERVIEW_COMPLETED,
    ASSESSMENT_COMPLETED,

    AWAITING_OFFER,
    OFFER_GIVEN,
    OFFER_DECLINED,
    OFFER_ACCEPTED,

    HIRED,
    REJECTED,
    WITHDRAWN;

    public boolean canTransitionTo(ApplicationStatus target) {
        Set<ApplicationStatus> allowed = switch (this) {
            case APPLIED -> EnumSet.of(FOR_INTERVIEW, FOR_ASSESSMENT, WITHDRAWN, REJECTED);
            case FOR_INTERVIEW -> EnumSet.of(INTERVIEW_COMPLETED, WITHDRAWN, REJECTED);
            case FOR_ASSESSMENT -> EnumSet.of(ASSESSMENT_COMPLETED, WITHDRAWN, REJECTED);
            case INTERVIEW_COMPLETED, ASSESSMENT_COMPLETED -> EnumSet.of(AWAITING_OFFER, WITHDRAWN, REJECTED);
            case AWAITING_OFFER -> EnumSet.of(OFFER_GIVEN, WITHDRAWN, REJECTED);
            case OFFER_GIVEN -> EnumSet.of(OFFER_ACCEPTED, OFFER_DECLINED, WITHDRAWN);
            case OFFER_ACCEPTED -> EnumSet.of(HIRED);
            default -> EnumSet.noneOf(ApplicationStatus.class);
        };

        return allowed.contains(target);
    }
}

/*
    APPLIED ->  FOR_INTERVIEW, FOR_ASSESSMENT, WITHDRAWN, REJECTED
    FOR_INTERVIEW -> INTERVIEW_COMPLETED, WITHDRAWN, REJECTED
    FOR_ASSESSMENT -> ASSESSMENT_COMPLETED, WITHDRAWN, REJECTED

    INTERVIEW_COMPLETED, ASSESSMENT_COMPLETED -> AWAITING_OFFER, REJECTED, WITHDRAWN
    AWAITING_OFFER -> OFFER_GIVEN, REJECTED, WITHDRAWN

    OFFER_GIVEN -> OFFER_ACCEPTED, OFFER_DECLINED, WITHDRAWN
    OFFER_ACCEPTED -> HIRED
 */
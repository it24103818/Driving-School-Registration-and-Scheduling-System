package com.drivinglearners.driving_learners;

import com.drivinglearners.driving_learners.model.RenewalRequest;
import java.time.LocalDate;

public class ReturningRenewalRequest extends RenewalRequest {
    public ReturningRenewalRequest(String learnerId, LocalDate requestDate) {
        super(learnerId, requestDate, false);
    }

    public String getProcessingMessage() {
        return "Returning learner renewal processed for " + getLearnerId();
    }
}
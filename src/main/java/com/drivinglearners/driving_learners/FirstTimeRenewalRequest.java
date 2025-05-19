package com.drivinglearners.driving_learners;

import com.drivinglearners.driving_learners.model.RenewalRequest;
import java.time.LocalDate;

public class FirstTimeRenewalRequest extends RenewalRequest {
    public FirstTimeRenewalRequest(String learnerId, LocalDate requestDate) {
        super(learnerId, requestDate, true);
    }

    public String getProcessingMessage() {
        return "First-time renewal processed for " + getLearnerId();
    }
}
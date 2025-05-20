package com.drivinglearners.driving_learners.model;

import java.time.LocalDate;

public class AdvancedLearner extends Learner {
    public AdvancedLearner(String id, String name, String email, String contact, LocalDate expiryDate) {
        super(id, name, email, contact, expiryDate, "Advanced");
    }

    @Override
    public String trackProgress() {
        return "Advanced progress for " + getName() + ": Mastering complex skills.";
    }

    @Override
    public boolean isEligibleForRenewal() {
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, getExpiryDate());
        return daysUntilExpiry <= 15;
    }

    @Override
    public String toString() {
        return getLicenseType() + "," + getId() + "," + getName() + "," + getEmail() + "," + getContact() + "," + getExpiryDate();
    }
}
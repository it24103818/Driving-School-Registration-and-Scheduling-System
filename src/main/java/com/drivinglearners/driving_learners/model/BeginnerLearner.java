package com.drivinglearners.driving_learners.model;

import java.time.LocalDate;

public class BeginnerLearner extends Learner {
    public BeginnerLearner(String id, String name, String email, String contact, LocalDate expiryDate) {
        super(id, name, email, contact, expiryDate, "Beginner");
    }

    @Override
    public String trackProgress() {
        return "Beginner progress for " + getName() + ": Basic skills in progress.";
    }

    @Override
    public boolean isEligibleForRenewal() {
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, getExpiryDate());
        return daysUntilExpiry <= 30;
    }

    @Override
    public String toString() {
        return getLicenseType() + "," + getId() + "," + getName() + "," + getEmail() + "," + getContact() + "," + getExpiryDate();
    }
}
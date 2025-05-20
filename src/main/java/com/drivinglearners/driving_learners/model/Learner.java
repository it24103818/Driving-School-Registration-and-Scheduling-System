package com.drivinglearners.driving_learners.model;

import java.time.LocalDate;

public abstract class Learner {
    private String id;
    private String name;
    private String email;
    private String contact;
    private LocalDate expiryDate;
    private String licenseType;

    public Learner(String id, String name, String email, String contact, LocalDate expiryDate, String licenseType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.expiryDate = expiryDate;
        this.licenseType = licenseType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public String trackProgress() {
        return "Tracking progress for " + name;
    }

    public abstract boolean isEligibleForRenewal();
}
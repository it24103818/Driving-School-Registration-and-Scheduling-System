package com.drivinglearners.driving_learners.model;

public class SuperAdmin extends Admin {
    public SuperAdmin(String adminId, String name, String email) {
        super(adminId, name, email, "FULL_ACCESS");
    }

    @Override
    public String performAdminAction(String action) {
        return "SuperAdmin " + getName() + " performed action: " + action + " with full access.";
    }
}
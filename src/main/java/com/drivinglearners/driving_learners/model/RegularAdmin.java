package com.drivinglearners.driving_learners.model;

public class RegularAdmin extends Admin {
    public RegularAdmin(String adminId, String name, String email) {
        super(adminId, name, email, "LIMITED_ACCESS");
    }

    @Override
    public String performAdminAction(String action) {
        return "RegularAdmin " + getName() + " performed action: " + action + " with limited access.";
    }
}
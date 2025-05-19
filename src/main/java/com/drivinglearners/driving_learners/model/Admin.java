package com.drivinglearners.driving_learners.model;

public abstract class Admin {
    private String adminId;
    private String name;
    private String email;
    private String permissions; // e.g., "FULL_ACCESS" or "LIMITED_ACCESS"

    public Admin(String adminId, String name, String email, String permissions) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.permissions = permissions;
    }

    // Getters and Setters (Encapsulation)
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    // Abstract method (Abstraction)
    public abstract String performAdminAction(String action);
}
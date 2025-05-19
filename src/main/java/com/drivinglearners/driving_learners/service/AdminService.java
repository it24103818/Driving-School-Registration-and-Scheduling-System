package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.Admin;
import com.drivinglearners.driving_learners.model.RegularAdmin;
import com.drivinglearners.driving_learners.model.SuperAdmin;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {
    private static final String FILE_PATH = "admins.txt";
    private static final String ACTIVITY_LOG_PATH = "admin_activities.txt";

    public void registerAdmin(Admin admin) throws IOException {
        // Check for duplicate admin ID or email
        List<Admin> admins = getAllAdmins();
        for (Admin existingAdmin : admins) {
            if (existingAdmin.getAdminId().equals(admin.getAdminId())) {
                throw new IllegalArgumentException("Admin with ID " + admin.getAdminId() + " already exists.");
            }
            if (existingAdmin.getEmail().equals(admin.getEmail())) {
                throw new IllegalArgumentException("Admin with email " + admin.getEmail() + " already exists.");
            }
        }

        // Save the admin
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(admin.getAdminId() + "," + admin.getName() + "," + admin.getEmail() + "," + admin.getPermissions());
            writer.newLine();
        }

        // Log the registration activity
        logActivity(admin, "Registered new admin with ID " + admin.getAdminId());
    }

    public List<Admin> getAllAdmins() throws IOException {
        List<Admin> admins = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return admins;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 4) continue;
                Admin admin = data[3].equals("FULL_ACCESS")
                        ? new SuperAdmin(data[0], data[1], data[2])
                        : new RegularAdmin(data[0], data[1], data[2]);
                admins.add(admin);
            }
        }
        return admins;
    }

    public Admin getAdminById(String adminId) throws IOException {
        List<Admin> admins = getAllAdmins();
        for (Admin admin : admins) {
            if (admin.getAdminId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }

    public void updateAdmin(Admin updatedAdmin) throws IOException {
        List<Admin> admins = getAllAdmins();
        boolean found = false;
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getAdminId().equals(updatedAdmin.getAdminId())) {
                // Check for email conflict with other admins
                for (Admin otherAdmin : admins) {
                    if (!otherAdmin.getAdminId().equals(updatedAdmin.getAdminId()) && otherAdmin.getEmail().equals(updatedAdmin.getEmail())) {
                        throw new IllegalArgumentException("Email " + updatedAdmin.getEmail() + " is already in use by another admin.");
                    }
                }
                admins.set(i, updatedAdmin);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Admin with ID " + updatedAdmin.getAdminId() + " not found.");
        }

        // Save updated admins
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Admin admin : admins) {
                writer.write(admin.getAdminId() + "," + admin.getName() + "," + admin.getEmail() + "," + admin.getPermissions());
                writer.newLine();
            }
        }

        // Log the update activity
        logActivity(updatedAdmin, "Updated admin details for ID " + updatedAdmin.getAdminId());
    }

    public void deleteAdmin(String adminId) throws IOException {
        List<Admin> admins = getAllAdmins();
        Admin adminToDelete = null;
        for (Admin admin : admins) {
            if (admin.getAdminId().equals(adminId)) {
                adminToDelete = admin;
                break;
            }
        }
        if (adminToDelete == null) {
            throw new IllegalArgumentException("Admin with ID " + adminId + " not found.");
        }
        admins.removeIf(admin -> admin.getAdminId().equals(adminId));

        // Save updated admins
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Admin admin : admins) {
                writer.write(admin.getAdminId() + "," + admin.getName() + "," + admin.getEmail() + "," + admin.getPermissions());
                writer.newLine();
            }
        }

        // Log the deletion activity
        logActivity(adminToDelete, "Deleted admin with ID " + adminId);
    }

    public List<String> getActivityLogs() throws IOException {
        List<String> logs = new ArrayList<>();
        File file = new File(ACTIVITY_LOG_PATH);
        if (!file.exists()) {
            return logs;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                logs.add(line);
            }
        }
        return logs;
    }

    private void logActivity(Admin admin, String action) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACTIVITY_LOG_PATH, true))) {
            String logEntry = admin.performAdminAction(action);
            writer.write(logEntry);
            writer.newLine();
        }
    }
}
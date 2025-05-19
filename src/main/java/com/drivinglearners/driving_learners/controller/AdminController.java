package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.Admin;
import com.drivinglearners.driving_learners.model.AdminDTO;
import com.drivinglearners.driving_learners.model.RegularAdmin;
import com.drivinglearners.driving_learners.model.SuperAdmin;
import com.drivinglearners.driving_learners.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/register")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("admin", new AdminDTO());
        return "adminRegister";
    }

    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute("admin") AdminDTO adminDTO, @RequestParam(required = false) String adminType, Model model) {
        try {
            logger.info("Registering admin: name={}, email={}, type={}", adminDTO.getName(), adminDTO.getEmail(), adminType);
            if (adminType == null || adminType.isEmpty()) {
                throw new IllegalArgumentException("Admin type is required.");
            }
            Admin newAdmin = adminType.equals("SuperAdmin")
                    ? new SuperAdmin(UUID.randomUUID().toString(), adminDTO.getName(), adminDTO.getEmail())
                    : new RegularAdmin(UUID.randomUUID().toString(), adminDTO.getName(), adminDTO.getEmail());
            adminService.registerAdmin(newAdmin);
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            logger.warn("IllegalArgumentException during admin registration: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "adminRegister";
        } catch (IOException e) {
            logger.error("IOException during admin registration: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to register admin: " + e.getMessage());
            return "adminRegister";
        } catch (Exception e) {
            logger.error("Unexpected error during admin registration: {}", e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "adminRegister";
        }
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        try {
            model.addAttribute("admins", adminService.getAllAdmins());
            model.addAttribute("activities", adminService.getActivityLogs());
            return "adminDashboard";
        } catch (IOException e) {
            logger.error("IOException in showAdminDashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "adminDashboard";
        }
    }

    @GetMapping("/management")
    public String showAdminManagementPanel(Model model) {
        try {
            model.addAttribute("admins", adminService.getAllAdmins());
            return "adminManagement";
        } catch (IOException e) {
            logger.error("IOException in showAdminManagementPanel: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load admin management panel: " + e.getMessage());
            return "adminManagement";
        }
    }

    @GetMapping("/update/{adminId}")
    public String showUpdateAdminForm(@PathVariable String adminId, Model model) {
        try {
            Admin admin = adminService.getAdminById(adminId);
            if (admin == null) {
                model.addAttribute("error", "Admin with ID " + adminId + " not found.");
                return "error";
            }
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setAdminId(admin.getAdminId());
            adminDTO.setName(admin.getName());
            adminDTO.setEmail(admin.getEmail());
            model.addAttribute("admin", adminDTO);
            return "adminRegister";
        } catch (IOException e) {
            logger.error("IOException in showUpdateAdminForm: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load admin: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/update")
    public String updateAdmin(@ModelAttribute("admin") AdminDTO adminDTO, @RequestParam(required = false) String adminType, Model model) {
        try {
            logger.info("Updating admin: id={}, name={}, email={}, type={}", adminDTO.getAdminId(), adminDTO.getName(), adminDTO.getEmail(), adminType);
            if (adminType == null || adminType.isEmpty()) {
                throw new IllegalArgumentException("Admin type is required.");
            }
            Admin updatedAdmin = adminType.equals("SuperAdmin")
                    ? new SuperAdmin(adminDTO.getAdminId(), adminDTO.getName(), adminDTO.getEmail())
                    : new RegularAdmin(adminDTO.getAdminId(), adminDTO.getName(), adminDTO.getEmail());
            adminService.updateAdmin(updatedAdmin);
            return "redirect:/admin/management";
        } catch (IllegalArgumentException e) {
            logger.warn("IllegalArgumentException during admin update: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "adminRegister";
        } catch (IOException e) {
            logger.error("IOException during admin update: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to update admin: " + e.getMessage());
            return "adminRegister";
        } catch (Exception e) {
            logger.error("Unexpected error during admin update: {}", e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "adminRegister";
        }
    }

    @PostMapping("/delete/{adminId}")
    public String deleteAdmin(@PathVariable String adminId, Model model) {
        try {
            logger.info("Deleting admin: id={}", adminId);
            adminService.deleteAdmin(adminId);
            return "redirect:/admin/management";
        } catch (IllegalArgumentException e) {
            logger.warn("IllegalArgumentException during admin deletion: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "adminManagement";
        } catch (IOException e) {
            logger.error("IOException during admin deletion: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to delete admin: " + e.getMessage());
            return "adminManagement";
        }
    }
}
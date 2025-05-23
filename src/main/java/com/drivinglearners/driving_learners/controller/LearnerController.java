package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.AdvancedLearner;
import com.drivinglearners.driving_learners.model.BeginnerLearner;
import com.drivinglearners.driving_learners.model.Learner;
import com.drivinglearners.driving_learners.model.SearchCriteria;
import com.drivinglearners.driving_learners.service.LearnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/learner") // Prefix all mappings with /learner
public class LearnerController {
    private final LearnerService learnerService;

    public LearnerController(LearnerService learnerService) {
        this.learnerService = learnerService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        System.out.println("Showing registration form at " + LocalDate.now());
        model.addAttribute("learner", new BeginnerLearner("", "", "", "", LocalDate.now()));
        return "register";
    }

    @PostMapping("/register")
    public String registerLearner(@ModelAttribute BeginnerLearner learner, @RequestParam String licenseType, Model model) {
        System.out.println("Received registration request: " + learner + ", LicenseType: " + licenseType);
        try {
            // Create the appropriate learner type based on the selected licenseType
            Learner newLearner = licenseType.equals("Beginner")
                    ? new BeginnerLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate())
                    : new AdvancedLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate());
            newLearner.setLicenseType(licenseType); // Ensure licenseType is set
            System.out.println("Checking for existing learner with ID: " + newLearner.getId());
            if (getLearnerById(newLearner.getId()) != null) {
                throw new IllegalArgumentException("Learner with ID " + newLearner.getId() + " already exists.");
            }
            System.out.println("Creating new learner: " + newLearner);
            learnerService.createLearner(newLearner);
            System.out.println("Learner created successfully, redirecting to view");
            return "redirect:/learner/view";
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            model.addAttribute("error", "Failed to save learner: " + e.getMessage());
            return "register";
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "register";
        }
    }

    // Helper method to check existing learner by ID
    private Learner getLearnerById(String id) throws IOException {
        return learnerService.getLearnerById(id);
    }

    @GetMapping("/view")
    public String listLearners(Model model, @ModelAttribute("searchCriteria") SearchCriteria searchCriteria) throws IOException {
        if (searchCriteria != null && (searchCriteria.getId() != null || searchCriteria.getEmail() != null)) {
            List<Learner> learners = learnerService.searchLearner(searchCriteria.getId(), searchCriteria.getEmail());
            model.addAttribute("learners", learners);
            model.addAttribute("searchPerformed", true);
            if (learners.isEmpty()) {
                model.addAttribute("error", "No learner found with the given ID or email.");
            }
        } else {
            model.addAttribute("learners", learnerService.getAllLearners());
            model.addAttribute("searchPerformed", false);
        }
        model.addAttribute("searchCriteria", new SearchCriteria());
        return "learner-list";
    }

    @GetMapping("/profile/{id}")
    public String viewLearnerProfile(@PathVariable String id, Model model) throws IOException {
        Learner learner = learnerService.getLearnerById(id);
        if (learner == null) {
            model.addAttribute("error", "Learner not found");
            return "error";
        }
        model.addAttribute("learner", learner);
        return "learner-profile";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) throws IOException {
        Learner learner = learnerService.getLearnerById(id);
        if (learner == null) {
            model.addAttribute("error", "Learner not found");
            return "error";
        }
        model.addAttribute("learner", learner);
        return "update-learner";
    }

    @PostMapping("/update")
    public String updateLearner(@ModelAttribute Learner learner, @RequestParam String licenseType) throws IOException {
        Learner updatedLearner = licenseType.equals("Beginner")
                ? new BeginnerLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate())
                : new AdvancedLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate());
        learnerService.updateLearner(updatedLearner);
        return "redirect:/learner/view";
    }
}
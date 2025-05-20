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
        // Use a default concrete subclass (e.g., BeginnerLearner) for form binding
        model.addAttribute("learner", new BeginnerLearner("", "", "", "", LocalDate.now()));
        return "learner-register";
    }

    @PostMapping("/register")
    public String registerLearner(@ModelAttribute Learner learner, @RequestParam String licenseType, Model model) {
        try {
            Learner newLearner = licenseType.equals("Beginner")
                    ? new BeginnerLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate())
                    : new AdvancedLearner(learner.getId(), learner.getName(), learner.getEmail(), learner.getContact(), learner.getExpiryDate());
            learnerService.createLearner(newLearner);
            return "redirect:/learner/view";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "learner-register";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to save learner: " + e.getMessage());
            return "learner-register";
        }
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
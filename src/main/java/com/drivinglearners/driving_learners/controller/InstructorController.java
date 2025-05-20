package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.FullTimeInstructor;
import com.drivinglearners.driving_learners.model.Instructor;
import com.drivinglearners.driving_learners.model.PartTimeInstructor;
import com.drivinglearners.driving_learners.service.InstructorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("instructor", new Instructor("", "", "", "", 0));
        return "instructor-register";
    }

    @PostMapping("/register")
    public String registerInstructor(@ModelAttribute Instructor instructor, @RequestParam String type, Model model) {
        try {
            Instructor newInstructor = type.equals("PartTime") ?
                    new PartTimeInstructor(instructor.getId(), instructor.getName(), instructor.getEmail(), instructor.getContact(), instructor.getExperience()) :
                    new FullTimeInstructor(instructor.getId(), instructor.getName(), instructor.getEmail(), instructor.getContact(), instructor.getExperience());
            instructorService.createInstructor(newInstructor);
            return "redirect:/instructor/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "instructor-register";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to save instructor: " + e.getMessage());
            return "instructor-register";
        }
    }

    @GetMapping("/list")
    public String listInstructors(Model model) throws IOException {
        model.addAttribute("instructors", instructorService.getAllInstructors());
        return "instructor-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteInstructor(@PathVariable String id) throws IOException {
        boolean deleted = instructorService.deleteInstructor(id);
        if (!deleted) {
            return "error";
        }
        return "redirect:/instructor/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) throws IOException {
        Instructor instructor = instructorService.getInstructorById(id);
        if (instructor == null) {
            model.addAttribute("error", "Instructor not found");
            return "error";
        }
        model.addAttribute("instructor", instructor);
        model.addAttribute("type", instructor instanceof PartTimeInstructor ? "PartTime" : "FullTime");
        return "update-instructor";
    }

    @PostMapping("/update")
    public String updateInstructor(@ModelAttribute Instructor instructor, @RequestParam String type, Model model) throws IOException {
        Instructor updatedInstructor = type.equals("PartTime") ?
                new PartTimeInstructor(instructor.getId(), instructor.getName(), instructor.getEmail(), instructor.getContact(), instructor.getExperience()) :
                new FullTimeInstructor(instructor.getId(), instructor.getName(), instructor.getEmail(), instructor.getContact(), instructor.getExperience());
        boolean updated = instructorService.updateInstructor(updatedInstructor);
        if (!updated) {
            model.addAttribute("error", "Failed to update instructor");
            return "update-instructor";
        }
        return "redirect:/instructor/list";
    }
}
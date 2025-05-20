package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.Lesson;
import com.drivinglearners.driving_learners.service.LessonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/scheduleLesson")
    public String showScheduleLessonForm(Model model) {
        model.addAttribute("lesson", new Lesson("", "", "", "", ""));
        return "scheduleLesson";
    }

    @PostMapping("/scheduleLesson")
    public String scheduleLesson(@ModelAttribute Lesson lesson, Model model) {
        try {
            lessonService.scheduleLesson(lesson);
            return "redirect:/viewLessons";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "scheduleLesson";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to schedule lesson: " + e.getMessage());
            return "scheduleLesson";
        }
    }

    @GetMapping("/viewLessons")
    public String viewLessons(Model model, @RequestParam(required = false) String learnerId, @RequestParam(required = false) String instructorId) {
        try {
            if (learnerId != null && !learnerId.isEmpty()) {
                model.addAttribute("lessons", lessonService.getLessonsByLearner(learnerId));
                model.addAttribute("filterType", "Learner ID: " + learnerId);
            } else if (instructorId != null && !instructorId.isEmpty()) {
                model.addAttribute("lessons", lessonService.getLessonsByInstructor(instructorId));
                model.addAttribute("filterType", "Instructor ID: " + instructorId);
            } else {
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("filterType", "All Lessons");
            }
            return "viewLessons";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to load lessons: " + e.getMessage());
            return "viewLessons";
        }
    }

    @GetMapping("/rescheduleLesson/{lessonId}")
    public String showRescheduleLessonForm(@PathVariable String lessonId, Model model) {
        try {
            Lesson lesson = lessonService.getLessonById(lessonId);
            if (lesson == null) {
                model.addAttribute("error", "Lesson with ID " + lessonId + " not found.");
                return "error";
            }
            model.addAttribute("lesson", lesson);
            return "rescheduleLesson";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to load lesson: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/rescheduleLesson")
    public String rescheduleLesson(@ModelAttribute Lesson lesson, Model model) {
        try {
            lessonService.updateLesson(lesson);
            return "redirect:/viewLessons";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("lesson", lesson);
            return "rescheduleLesson";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to reschedule lesson: " + e.getMessage());
            model.addAttribute("lesson", lesson);
            return "rescheduleLesson";
        }
    }

    @PostMapping("/cancelLesson/{lessonId}")
    public String cancelLesson(@PathVariable String lessonId, Model model) {
        try {
            lessonService.deleteLesson(lessonId);
            return "redirect:/viewLessons";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "viewLessons";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to cancel lesson: " + e.getMessage());
            return "viewLessons";
        }
    }
}
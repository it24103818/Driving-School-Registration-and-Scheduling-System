package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.RenewalRequest;
import com.drivinglearners.driving_learners.service.RenewalQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Queue;

@Controller
public class RenewalQueueController {

    @Autowired
    private RenewalQueueService queueService;

    @GetMapping("/submitRequest")
    public String showSubmitRequestForm() {
        return "submitRequest";
    }

    @PostMapping("/submitRequest")
    public String submitRequest(
            @RequestParam String learnerId,
            @RequestParam boolean isFirstTime,
            Model model) {
        if (queueService.isEligibleForRenewal(learnerId)) {
            queueService.submitRequest(learnerId, isFirstTime);
            model.addAttribute("message", "Renewal request submitted successfully!");
        } else {
            model.addAttribute("message", "You are not eligible for renewal.");
        }
        return "submitRequest";
    }

    @GetMapping("/viewQueue")
    public String viewQueue(Model model) {
        Queue<RenewalRequest> queue = queueService.getQueue();
        model.addAttribute("queue", queue);
        return "viewQueue";
    }

    @GetMapping("/processRequest")
    public String showProcessRequestForm(Model model) {
        RenewalRequest nextRequest = queueService.peekNextRequest(); // Peek at the next request without removing it
        model.addAttribute("nextRequest", nextRequest);
        return "processRequest";
    }

    @PostMapping("/processRequest")
    public String processRequest(@RequestParam boolean approve, Model model) {
        String message = queueService.processRequest(approve);
        if (message != null) {
            model.addAttribute("message", message);
        } else {
            model.addAttribute("message", "No requests to process.");
        }
        RenewalRequest nextRequest = queueService.peekNextRequest(); // Update next request after processing
        model.addAttribute("nextRequest", nextRequest);
        return "processRequest";
    }
}
package com.drivinglearners.driving_learners.controller;

import com.drivinglearners.driving_learners.model.InstructorReview;
import com.drivinglearners.driving_learners.model.LessonReview;
import com.drivinglearners.driving_learners.model.Review;
import com.drivinglearners.driving_learners.model.ReviewDTO;
import com.drivinglearners.driving_learners.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/submit")
    public String showFeedbackSubmissionForm(Model model) {
        model.addAttribute("reviewDTO", new ReviewDTO());
        return "submitFeedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute("reviewDTO") ReviewDTO reviewDTO, Model model) {
        try {
            logger.info("Submitting review: learnerId={}, reviewType={}", reviewDTO.getLearnerId(), reviewDTO.getReviewType());
            Review review;
            String reviewId = UUID.randomUUID().toString().substring(0, 8); // Shortened review ID
            if ("Instructor".equals(reviewDTO.getReviewType())) {
                review = new InstructorReview(reviewId, reviewDTO.getLearnerId(), reviewDTO.getInstructorId(),
                        reviewDTO.getRating(), reviewDTO.getComment());
            } else if ("Lesson".equals(reviewDTO.getReviewType())) {
                review = new LessonReview(reviewId, reviewDTO.getLearnerId(), reviewDTO.getLessonId(),
                        reviewDTO.getRating(), reviewDTO.getComment());
            } else {
                throw new IllegalArgumentException("Invalid review type");
            }
            reviewService.submitReview(review);
            return "redirect:/reviews/view";
        } catch (IllegalArgumentException e) {
            logger.warn("IllegalArgumentException during review submission: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "submitFeedback";
        } catch (IOException e) {
            logger.error("IOException during review submission: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to submit review: " + e.getMessage());
            return "submitFeedback";
        }
    }

    @GetMapping("/view")
    public String viewReviews(Model model, @RequestParam(required = false) String instructorId,
                              @RequestParam(required = false) String lessonId) {
        try {
            if (instructorId != null) {
                model.addAttribute("reviews", reviewService.getReviewsForInstructor(instructorId));
            } else if (lessonId != null) {
                model.addAttribute("reviews", reviewService.getReviewsForLesson(lessonId));
            } else {
                model.addAttribute("reviews", reviewService.getAllReviews());
            }
            return "viewReviews";
        } catch (IOException e) {
            logger.error("IOException in viewReviews: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load reviews: " + e.getMessage());
            return "viewReviews";
        }
    }

    @GetMapping("/edit/{reviewId}")
    public String showEditReviewForm(@PathVariable String reviewId, Model model) {
        try {
            Review review = reviewService.getReviewById(reviewId);
            if (review == null) {
                model.addAttribute("error", "Review with ID " + reviewId + " not found.");
                return "error";
            }
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setReviewId(review.getReviewId());
            reviewDTO.setLearnerId(review.getLearnerId());
            reviewDTO.setRating(review.getRating());
            reviewDTO.setComment(review.getComment());
            if (review instanceof InstructorReview instructorReview) {
                reviewDTO.setInstructorId(instructorReview.getInstructorId());
                reviewDTO.setReviewType("Instructor");
            } else if (review instanceof LessonReview lessonReview) {
                reviewDTO.setLessonId(lessonReview.getLessonId());
                reviewDTO.setReviewType("Lesson");
            }
            model.addAttribute("reviewDTO", reviewDTO);
            return "submitFeedback";
        } catch (IOException e) {
            logger.error("IOException in showEditReviewForm: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load review: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/edit")
    public String editReview(@ModelAttribute("reviewDTO") ReviewDTO reviewDTO, Model model) {
        try {
            logger.info("Editing review: reviewId={}, reviewType={}", reviewDTO.getReviewId(), reviewDTO.getReviewType());
            Review review;
            if ("Instructor".equals(reviewDTO.getReviewType())) {
                review = new InstructorReview(reviewDTO.getReviewId(), reviewDTO.getLearnerId(),
                        reviewDTO.getInstructorId(), reviewDTO.getRating(), reviewDTO.getComment());
            } else if ("Lesson".equals(reviewDTO.getReviewType())) {
                review = new LessonReview(reviewDTO.getReviewId(), reviewDTO.getLearnerId(),
                        reviewDTO.getLessonId(), reviewDTO.getRating(), reviewDTO.getComment());
            } else {
                throw new IllegalArgumentException("Invalid review type");
            }
            reviewService.updateReview(review);
            return "redirect:/reviews/view";
        } catch (IllegalArgumentException e) {
            logger.warn("IllegalArgumentException during review edit: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "submitFeedback";
        } catch (IOException e) {
            logger.error("IOException during review edit: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to edit review: " + e.getMessage());
            return "submitFeedback";
        }
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable String reviewId, Model model) {
        try {
            logger.info("Deleting review: reviewId={}", reviewId);
            reviewService.deleteReview(reviewId);
            return "redirect:/reviews/moderate";
        } catch (IOException e) {
            logger.error("IOException during review deletion: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to delete review: " + e.getMessage());
            return "adminReviewModeration";
        }
    }

    @GetMapping("/moderate")
    public String showAdminReviewModerationPanel(Model model) {
        try {
            model.addAttribute("reviews", reviewService.getAllReviews());
            return "adminReviewModeration";
        } catch (IOException e) {
            logger.error("IOException in showAdminReviewModerationPanel: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load reviews: " + e.getMessage());
            return "adminReviewModeration";
        }
    }
}
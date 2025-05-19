package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.InstructorReview;
import com.drivinglearners.driving_learners.model.LessonReview;
import com.drivinglearners.driving_learners.model.Review;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private static final String REVIEWS_DIR = System.getProperty("user.home") + "/driving_learners/";
    private static final String REVIEWS_FILE = REVIEWS_DIR + "reviews.txt";

    public ReviewService() throws IOException {
        File dir = new File(REVIEWS_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // Creates the directory
        }
        File file = new File(REVIEWS_FILE);
        if (!file.exists()) {
            file.createNewFile(); // Creates the file if it doesn't exist

        }
    }

    public void submitReview(Review review) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEWS_FILE, true))) {
            String reviewLine;
            if (review instanceof InstructorReview instructorReview) {
                reviewLine = String.format("Instructor,%s,%s,%s,%d,%s",
                        review.getReviewId(), review.getLearnerId(), instructorReview.getInstructorId(),
                        review.getRating(), review.getComment());
            } else if (review instanceof LessonReview lessonReview) {
                reviewLine = String.format("Lesson,%s,%s,%s,%d,%s",
                        review.getReviewId(), review.getLearnerId(), lessonReview.getLessonId(),
                        review.getRating(), review.getComment());
            } else {
                throw new IllegalArgumentException("Unknown review type");
            }
            writer.write(reviewLine);
            writer.newLine();
        }
    }

    public List<Review> getAllReviews() throws IOException {
        List<Review> reviews = new ArrayList<>();
        File file = new File(REVIEWS_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                String type = parts[0];
                String reviewId = parts[1];
                String learnerId = parts[2];
                String targetId = parts[3];
                int rating = Integer.parseInt(parts[4]);
                String comment = parts.length > 5 ? parts[5] : "";
                Review review;
                if ("Instructor".equals(type)) {
                    review = new InstructorReview(reviewId, learnerId, targetId, rating, comment);
                } else if ("Lesson".equals(type)) {
                    review = new LessonReview(reviewId, learnerId, targetId, rating, comment);
                } else {
                    continue;
                }
                reviews.add(review);
            }
        }
        return reviews;
    }

    public Review getReviewById(String reviewId) throws IOException {
        return getAllReviews().stream()
                .filter(review -> review.getReviewId().equals(reviewId))
                .findFirst()
                .orElse(null);
    }

    public List<Review> getReviewsForInstructor(String instructorId) throws IOException {
        return getAllReviews().stream()
                .filter(review -> review instanceof InstructorReview)
                .map(review -> (InstructorReview) review)
                .filter(review -> review.getInstructorId().equals(instructorId))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsForLesson(String lessonId) throws IOException {
        return getAllReviews().stream()
                .filter(review -> review instanceof LessonReview)
                .map(review -> (LessonReview) review)
                .filter(review -> review.getLessonId().equals(lessonId))
                .collect(Collectors.toList());
    }

    public void updateReview(Review updatedReview) throws IOException {
        List<Review> reviews = getAllReviews();
        reviews = reviews.stream()
                .map(review -> review.getReviewId().equals(updatedReview.getReviewId()) ? updatedReview : review)
                .collect(Collectors.toList());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEWS_FILE))) {
            for (Review review : reviews) {
                String reviewLine;
                if (review instanceof InstructorReview instructorReview) {
                    reviewLine = String.format("Instructor,%s,%s,%s,%d,%s",
                            review.getReviewId(), review.getLearnerId(), instructorReview.getInstructorId(),
                            review.getRating(), review.getComment());
                } else if (review instanceof LessonReview lessonReview) {
                    reviewLine = String.format("Lesson,%s,%s,%s,%d,%s",
                            review.getReviewId(), review.getLearnerId(), lessonReview.getLessonId(),
                            review.getRating(), review.getComment());
                } else {
                    continue;
                }
                writer.write(reviewLine);
                writer.newLine();
            }
        }
    }

    public void deleteReview(String reviewId) throws IOException {
        List<Review> reviews = getAllReviews();
        reviews = reviews.stream()
                .filter(review -> !review.getReviewId().equals(reviewId))
                .collect(Collectors.toList());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEWS_FILE))) {
            for (Review review : reviews) {
                String reviewLine;
                if (review instanceof InstructorReview instructorReview) {
                    reviewLine = String.format("Instructor,%s,%s,%s,%d,%s",
                            review.getReviewId(), review.getLearnerId(), instructorReview.getInstructorId(),
                            review.getRating(), review.getComment());
                } else if (review instanceof LessonReview lessonReview) {
                    reviewLine = String.format("Lesson,%s,%s,%s,%d,%s",
                            review.getReviewId(), review.getLearnerId(), lessonReview.getLessonId(),
                            review.getRating(), review.getComment());
                } else {
                    continue;
                }
                writer.write(reviewLine);
                writer.newLine();
            }
        }
    }
}
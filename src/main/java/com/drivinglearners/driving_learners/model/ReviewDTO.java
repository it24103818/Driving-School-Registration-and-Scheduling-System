package com.drivinglearners.driving_learners.model;

public class ReviewDTO {
    private String reviewId;
    private String learnerId;
    private String instructorId;
    private String lessonId;
    private int rating;
    private String comment;
    private String reviewType; // "Instructor" or "Lesson"

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getReviewType() { return reviewType; }
    public void setReviewType(String reviewType) { this.reviewType = reviewType; }
}
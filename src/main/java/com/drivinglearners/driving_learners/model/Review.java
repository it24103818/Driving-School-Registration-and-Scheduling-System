package com.drivinglearners.driving_learners.model;

public abstract class Review {
    private String reviewId;
    private String learnerId;
    private int rating; // 1 to 5
    private String comment;

    public Review(String reviewId, String learnerId, int rating, String comment) {
        this.reviewId = reviewId;
        this.learnerId = learnerId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters (Encapsulation)
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    // Polymorphic method to display review details
    public abstract String displayReview();
}
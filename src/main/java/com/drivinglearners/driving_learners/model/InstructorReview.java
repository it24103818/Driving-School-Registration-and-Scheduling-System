package com.drivinglearners.driving_learners.model;

public class InstructorReview extends Review {
    private String instructorId;

    public InstructorReview(String reviewId, String learnerId, String instructorId, int rating, String comment) {
        super(reviewId, learnerId, rating, comment);
        this.instructorId = instructorId;
    }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    @Override
    public String displayReview() {
        return String.format("Instructor Review [ID: %s, Learner: %s, Instructor: %s, Rating: %d, Comment: %s]",
                getReviewId(), getLearnerId(), instructorId, getRating(), getComment());
    }
}
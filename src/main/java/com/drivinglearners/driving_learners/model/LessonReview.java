package com.drivinglearners.driving_learners.model;

public class LessonReview extends Review {
    private String lessonId;

    public LessonReview(String reviewId, String learnerId, String lessonId, int rating, String comment) {
        super(reviewId, learnerId, rating, comment);
        this.lessonId = lessonId;
    }

    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    @Override
    public String displayReview() {
        return String.format("Lesson Review [ID: %s, Learner: %s, Lesson: %s, Rating: %d, Comment: %s]",
                getReviewId(), getLearnerId(), lessonId, getRating(), getComment());
    }
}
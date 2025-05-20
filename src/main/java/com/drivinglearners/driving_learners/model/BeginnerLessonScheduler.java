package com.drivinglearners.driving_learners.model;

import java.util.List;

public class BeginnerLessonScheduler extends LessonScheduler {
    @Override
    public boolean validateAvailability(List<Lesson> existingLessons, Lesson newLesson) {
        // Beginner rule: No lessons on the same day for the learner or instructor
        for (Lesson lesson : existingLessons) {
            if (lesson.getLearnerId().equals(newLesson.getLearnerId()) && lesson.getDate().equals(newLesson.getDate())) {
                return false; // Learner already has a lesson on this day
            }
            if (lesson.getInstructorId().equals(newLesson.getInstructorId()) && lesson.getDate().equals(newLesson.getDate())) {
                return false; // Instructor already has a lesson on this day
            }
        }
        return true;
    }
}
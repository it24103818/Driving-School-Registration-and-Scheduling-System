package com.drivinglearners.driving_learners.model;

import java.util.List;

public class AdvancedLessonScheduler extends LessonScheduler {
    @Override
    public boolean validateAvailability(List<Lesson> existingLessons, Lesson newLesson) {
        // Advanced rule: No overlapping times on the same day for learner or instructor
        for (Lesson lesson : existingLessons) {
            if (lesson.getDate().equals(newLesson.getDate())) {
                if (lesson.getLearnerId().equals(newLesson.getLearnerId()) && lesson.getTime().equals(newLesson.getTime())) {
                    return false; // Learner has a lesson at the same time
                }
                if (lesson.getInstructorId().equals(newLesson.getInstructorId()) && lesson.getTime().equals(newLesson.getTime())) {
                    return false; // Instructor has a lesson at the same time
                }
            }
        }
        return true;
    }
}
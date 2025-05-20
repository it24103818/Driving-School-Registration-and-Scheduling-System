package com.drivinglearners.driving_learners.model;

import java.util.List;

public abstract class LessonScheduler {
    public abstract boolean validateAvailability(List<Lesson> existingLessons, Lesson newLesson);
}
package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LessonService {
    private static final String FILE_PATH = "lessons.txt";

    private final LearnerService learnerService;
    private final InstructorService instructorService;

    @Autowired
    public LessonService(LearnerService learnerService, InstructorService instructorService) {
        this.learnerService = learnerService;
        this.instructorService = instructorService;
    }

    public void scheduleLesson(Lesson lesson) throws IOException {
        // Validate learner and instructor existence
        Learner learner = learnerService.getLearnerById(lesson.getLearnerId());
        if (learner == null) {
            throw new IllegalArgumentException("Learner with ID " + lesson.getLearnerId() + " does not exist.");
        }
        if (instructorService.getInstructorById(lesson.getInstructorId()) == null) {
            throw new IllegalArgumentException("Instructor with ID " + lesson.getInstructorId() + " does not exist.");
        }

        // Generate a unique lesson ID
        lesson.setLessonId(UUID.randomUUID().toString());

        // Determine scheduler based on learner type
        LessonScheduler scheduler = learner instanceof BeginnerLearner
                ? new BeginnerLessonScheduler()
                : new AdvancedLessonScheduler();

        // Validate availability
        List<Lesson> existingLessons = getAllLessons();
        if (!scheduler.validateAvailability(existingLessons, lesson)) {
            throw new IllegalArgumentException("Scheduling conflict: Lesson cannot be scheduled at this time.");
        }

        // Save the lesson
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(lesson.getLessonId() + "," + lesson.getLearnerId() + "," + lesson.getInstructorId() + ","
                    + lesson.getDate() + "," + lesson.getTime());
            writer.newLine();
        }
    }

    public List<Lesson> getAllLessons() throws IOException {
        List<Lesson> lessons = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return lessons;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 5) continue;
                lessons.add(new Lesson(data[0], data[1], data[2], data[3], data[4]));
            }
        }
        return lessons;
    }

    public List<Lesson> getLessonsByLearner(String learnerId) throws IOException {
        List<Lesson> lessons = getAllLessons();
        List<Lesson> learnerLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getLearnerId().equals(learnerId)) {
                learnerLessons.add(lesson);
            }
        }
        return learnerLessons;
    }

    public List<Lesson> getLessonsByInstructor(String instructorId) throws IOException {
        List<Lesson> lessons = getAllLessons();
        List<Lesson> instructorLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getInstructorId().equals(instructorId)) {
                instructorLessons.add(lesson);
            }
        }
        return instructorLessons;
    }

    public Lesson getLessonById(String lessonId) throws IOException {
        List<Lesson> lessons = getAllLessons();
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    public void updateLesson(Lesson updatedLesson) throws IOException {
        List<Lesson> lessons = getAllLessons();
        boolean found = false;
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(updatedLesson.getLessonId())) {
                lessons.set(i, updatedLesson);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Lesson with ID " + updatedLesson.getLessonId() + " not found.");
        }

        // Validate updated lesson for scheduling conflicts
        Learner learner = learnerService.getLearnerById(updatedLesson.getLearnerId());
        LessonScheduler scheduler = learner instanceof BeginnerLearner
                ? new BeginnerLessonScheduler()
                : new AdvancedLessonScheduler();

        List<Lesson> otherLessons = new ArrayList<>(lessons);
        otherLessons.removeIf(lesson -> lesson.getLessonId().equals(updatedLesson.getLessonId()));
        if (!scheduler.validateAvailability(otherLessons, updatedLesson)) {
            throw new IllegalArgumentException("Scheduling conflict: Lesson cannot be rescheduled at this time.");
        }

        // Save updated lessons
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Lesson lesson : lessons) {
                writer.write(lesson.getLessonId() + "," + lesson.getLearnerId() + "," + lesson.getInstructorId() + ","
                        + lesson.getDate() + "," + lesson.getTime());
                writer.newLine();
            }
        }
    }

    public void deleteLesson(String lessonId) throws IOException {
        List<Lesson> lessons = getAllLessons();
        boolean removed = lessons.removeIf(lesson -> lesson.getLessonId().equals(lessonId));
        if (!removed) {
            throw new IllegalArgumentException("Lesson with ID " + lessonId + " not found.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Lesson lesson : lessons) {
                writer.write(lesson.getLessonId() + "," + lesson.getLearnerId() + "," + lesson.getInstructorId() + ","
                        + lesson.getDate() + "," + lesson.getTime());
                writer.newLine();
            }
        }
    }
}
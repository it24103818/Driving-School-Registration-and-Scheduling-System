package com.drivinglearners.driving_learners.model;

public class Lesson {
    private String lessonId;
    private String learnerId;
    private String instructorId;
    private String date; // Format: YYYY-MM-DD
    private String time; // Format: HH:MM (24-hour)

    public Lesson(String lessonId, String learnerId, String instructorId, String date, String time) {
        this.lessonId = lessonId;
        this.learnerId = learnerId;
        this.instructorId = instructorId;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
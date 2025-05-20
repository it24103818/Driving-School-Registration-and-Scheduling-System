package com.drivinglearners.driving_learners.model;

public class FullTimeInstructor extends Instructor {
    public FullTimeInstructor(String id, String name, String email, String contact, int experience) {
        super(id, name, email, contact, experience);
    }

    @Override
    public String toString() {
        return "FullTime," + getId() + "," + getName() + "," + getEmail() + "," + getContact() + "," + getExperience();
    }
}
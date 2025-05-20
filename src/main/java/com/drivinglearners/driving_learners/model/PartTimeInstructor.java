package com.drivinglearners.driving_learners.model;

public class PartTimeInstructor extends Instructor {
    public PartTimeInstructor(String id, String name, String email, String contact, int experience) {
        super(id, name, email, contact, experience);
    }

    @Override
    public String toString() {
        return "PartTime," + getId() + "," + getName() + "," + getEmail() + "," + getContact() + "," + getExperience();
    }
}
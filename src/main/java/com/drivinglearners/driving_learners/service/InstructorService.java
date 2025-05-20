package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.FullTimeInstructor;
import com.drivinglearners.driving_learners.model.Instructor;
import com.drivinglearners.driving_learners.model.PartTimeInstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstructorService {
    private static final String FILE_PATH = "instructors.txt";

    public void createInstructor(Instructor instructor) throws IOException {
        if (instructor == null || instructor.getId() == null || instructor.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be null or empty.");
        }
        if (instructor.getName() == null || instructor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor name cannot be null or empty.");
        }
        if (instructor.getEmail() == null || instructor.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor email cannot be null or empty.");
        }
        if (instructor.getContact() == null || instructor.getContact().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor contact cannot be null or empty.");
        }

        if (getInstructorById(instructor.getId()) != null) {
            throw new IllegalArgumentException("Instructor with ID " + instructor.getId() + " already exists.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(instructor.toString());
            writer.newLine();
        }
    }

    public List<Instructor> getAllInstructors() throws IOException {
        List<Instructor> instructors = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return instructors;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 6) continue; // Updated to 6 fields (type + 5 existing fields)
                try {
                    Instructor instructor;
                    if (data[0].equals("PartTime")) {
                        instructor = new PartTimeInstructor(data[1], data[2], data[3], data[4], Integer.parseInt(data[5]));
                    } else {
                        instructor = new FullTimeInstructor(data[1], data[2], data[3], data[4], Integer.parseInt(data[5]));
                    }
                    instructors.add(instructor);
                } catch (Exception e) {
                    System.err.println("Error parsing line in instructors.txt: " + line + " - " + e.getMessage());
                }
            }
        }
        // Bubble sort by experience
        for (int i = 0; i < instructors.size() - 1; i++) {
            for (int j = 0; j < instructors.size() - i - 1; j++) {
                if (instructors.get(j).getExperience() > instructors.get(j + 1).getExperience()) {
                    Instructor temp = instructors.get(j);
                    instructors.set(j, instructors.get(j + 1));
                    instructors.set(j + 1, temp);
                }
            }
        }
        return instructors;
    }

    public Instructor getInstructorById(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 6) continue; // Updated to 6 fields
                if (data[1].equals(id)) { // Index 1 is now the ID
                    if (data[0].equals("PartTime")) {
                        return new PartTimeInstructor(data[1], data[2], data[3], data[4], Integer.parseInt(data[5]));
                    } else {
                        return new FullTimeInstructor(data[1], data[2], data[3], data[4], Integer.parseInt(data[5]));
                    }
                }
            }
        }
        return null;
    }

    public boolean deleteInstructor(String id) throws IOException {
        List<Instructor> instructors = getAllInstructors();
        boolean removed = instructors.removeIf(instructor -> instructor.getId().equals(id));
        if (removed) {
            saveInstructors(instructors);
            return true;
        }
        return false;
    }

    public boolean updateInstructor(Instructor updatedInstructor) throws IOException {
        List<Instructor> instructors = getAllInstructors();
        boolean found = false;
        for (int i = 0; i < instructors.size(); i++) {
            if (instructors.get(i).getId().equals(updatedInstructor.getId())) {
                instructors.set(i, updatedInstructor);
                found = true;
                break;
            }
        }
        if (found) {
            saveInstructors(instructors);
            return true;
        }
        return false;
    }

    private void saveInstructors(List<Instructor> instructors) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Instructor instructor : instructors) {
                writer.write(instructor.toString());
                writer.newLine();
            }
        }
    }
}
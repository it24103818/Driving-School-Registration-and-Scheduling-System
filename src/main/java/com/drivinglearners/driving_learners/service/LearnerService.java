package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.AdvancedLearner;
import com.drivinglearners.driving_learners.model.BeginnerLearner;
import com.drivinglearners.driving_learners.model.Learner;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LearnerService {
    private static final String FILE_PATH = "learners.txt";

    public void createLearner(Learner learner) throws IOException {
        if (getLearnerById(learner.getId()) != null) {
            throw new IllegalArgumentException("Learner with ID " + learner.getId() + " already exists.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(learner.toString());
            writer.newLine();
        }
    }

    private void saveLearners(List<Learner> learners) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Learner learner : learners) {
                writer.write(learner.toString());
                writer.newLine();
            }
        }
    }

    public List<Learner> getAllLearners() throws IOException {
        List<Learner> learners = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 6) continue; // Skip malformed lines
                try {
                    LocalDate expiryDate = LocalDate.parse(data[5]);
                    Learner learner = data[3].equals("Beginner")
                            ? new BeginnerLearner(data[0], data[1], data[2], data[4], expiryDate)
                            : new AdvancedLearner(data[0], data[1], data[2], data[4], expiryDate);
                    learners.add(learner);
                } catch (Exception e) {
                    System.err.println("Error parsing line in learners.txt: " + line + " - " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            return learners;
        }
        return learners;
    }


    public Learner getLearnerById(String id) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 6) continue;
                if (data[0].equals(id)) {
                    LocalDate expiryDate = LocalDate.parse(data[5]);
                    if (data[3].equals("Beginner")) {
                        return new BeginnerLearner(data[0], data[1], data[2], data[4], expiryDate);
                    } else {
                        return new AdvancedLearner(data[0], data[1], data[2], data[4], expiryDate);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading learner by ID: " + id + " - " + e.getMessage());
        }
        return null;
    }

    public boolean updateLearner(Learner updatedLearner) throws IOException {
        List<Learner> learners = getAllLearners();
        boolean found = false;
        for (int i = 0; i < learners.size(); i++) {
            if (learners.get(i).getId().equals(updatedLearner.getId())) {
                learners.set(i, updatedLearner);
                found = true;
                break;
            }
        }
        if (found) {
            saveLearners(learners);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteLearner(String id) throws IOException {
        List<Learner> learners = getAllLearners();
        boolean removed = learners.removeIf(learner -> learner.getId().equals(id));
        if (removed) {
            saveLearners(learners);
            return true;
        } else {
            return false;
        }
    }


    public List<Learner> searchLearner(String id, String email) throws IOException {
        List<Learner> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 6) continue;
                if ((id != null && !id.trim().isEmpty() && data[0].equals(id)) ||
                        (email != null && !email.trim().isEmpty() && data[2].equals(email))) {
                    LocalDate expiryDate = LocalDate.parse(data[5]);
                    Learner learner = data[3].equals("Beginner")
                            ? new BeginnerLearner(data[0], data[1], data[2], data[4], expiryDate)
                            : new AdvancedLearner(data[0], data[1], data[2], data[4], expiryDate);
                    results.add(learner);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching learners: " + e.getMessage());
        }
        return results;
    }
}
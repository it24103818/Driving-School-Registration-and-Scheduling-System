package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.FirstTimeRenewalRequest;
import com.drivinglearners.driving_learners.ReturningRenewalRequest;
import com.drivinglearners.driving_learners.model.RenewalRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RenewalQueueService {
    private final Queue<RenewalRequest> renewalQueue = new LinkedList<>();
    private final String FILE_PATH = "requests.txt";
    private final AtomicLong requestIdCounter = new AtomicLong(1);

    public RenewalQueueService() {
        loadRequestsFromFile();
    }

    public void submitRequest(String learnerId, boolean isFirstTime) {
        RenewalRequest request;
        if (isFirstTime) {
            request = new FirstTimeRenewalRequest(learnerId, LocalDate.now());
        } else {
            request = new ReturningRenewalRequest(learnerId, LocalDate.now());
        }
        request.setRequestId(requestIdCounter.getAndIncrement());
        renewalQueue.add(request);
        saveRequestsToFile();
    }

    public Queue<RenewalRequest> getQueue() {
        return renewalQueue;
    }

    public RenewalRequest peekNextRequest() {
        return renewalQueue.peek(); // Returns the next request without removing it
    }

    public String processRequest(boolean approve) {
        RenewalRequest request = renewalQueue.poll();
        if (request == null) {
            return null;
        }
        request.setStatus(approve ? "Approved" : "Denied");
        saveRequestsToFile();
        if (request instanceof FirstTimeRenewalRequest) {
            return ((FirstTimeRenewalRequest) request).getProcessingMessage();
        } else {
            return ((ReturningRenewalRequest) request).getProcessingMessage();
        }
    }

    public boolean isEligibleForRenewal(String learnerId) {
        File file = new File("learners.txt");
        if (!file.exists()) {
            return true;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 6) continue;
                if (parts[0].equals(learnerId)) {
                    LocalDate expiryDate = LocalDate.parse(parts[5]);
                    return expiryDate.isBefore(LocalDate.now()) || expiryDate.isEqual(LocalDate.now());
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking eligibility for learner ID " + learnerId + ": " + e.getMessage());
            return true;
        }
        return true;
    }

    private void loadRequestsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating requests.txt: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    RenewalRequest request;
                    if (parts.length == 3) {
                        String learnerId = parts[0];
                        LocalDate requestDate = LocalDate.parse(parts[1]);
                        boolean isFirstTime = Boolean.parseBoolean(parts[2]);
                        if (isFirstTime) {
                            request = new FirstTimeRenewalRequest(learnerId, requestDate);
                        } else {
                            request = new ReturningRenewalRequest(learnerId, requestDate);
                        }
                        request.setRequestId(requestIdCounter.getAndIncrement());
                        request.setStatus("Pending");
                    } else if (parts.length == 5) {
                        Long requestId = Long.parseLong(parts[0]);
                        String learnerId = parts[1];
                        LocalDate requestDate = LocalDate.parse(parts[2]);
                        boolean isFirstTime = Boolean.parseBoolean(parts[3]);
                        String status = parts[4];
                        if (isFirstTime) {
                            request = new FirstTimeRenewalRequest(learnerId, requestDate);
                        } else {
                            request = new ReturningRenewalRequest(learnerId, requestDate);
                        }
                        request.setRequestId(requestId);
                        request.setStatus(status);
                        requestIdCounter.set(Math.max(requestIdCounter.get(), requestId + 1));
                    } else {
                        continue;
                    }
                    renewalQueue.add(request);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading requests.txt: " + e.getMessage());
        }
    }

    private void saveRequestsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (RenewalRequest request : renewalQueue) {
                writer.write(request.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
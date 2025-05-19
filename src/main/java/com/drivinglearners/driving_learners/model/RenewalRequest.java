package com.drivinglearners.driving_learners.model;

import com.drivinglearners.driving_learners.FirstTimeRenewalRequest;
import com.drivinglearners.driving_learners.ReturningRenewalRequest;

import java.time.LocalDate;

public class RenewalRequest {
    private Long requestId; // Add requestId for unique identification
    private String learnerId;
    private LocalDate requestDate;
    private boolean isFirstTime;
    private String status; // Add status to track request state (Pending/Approved/Denied)

    public RenewalRequest(String learnerId, LocalDate requestDate, boolean isFirstTime) {
        this.requestId = null; // Will be set by the service
        this.learnerId = learnerId;
        this.requestDate = requestDate;
        this.isFirstTime = isFirstTime;
        this.status = "Pending"; // Default status
    }

    // Add a constructor that includes requestId and status for loading from file
    public RenewalRequest(Long requestId, String learnerId, LocalDate requestDate, boolean isFirstTime, String status) {
        this.requestId = requestId;
        this.learnerId = learnerId;
        this.requestDate = requestDate;
        this.isFirstTime = isFirstTime;
        this.status = status;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(String learnerId) {
        this.learnerId = learnerId;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Updated toString for file storage
    @Override
    public String toString() {
        return requestId + "," + learnerId + "," + requestDate.toString() + "," + isFirstTime + "," + status;
    }

    // Updated static method to parse a string back into a RenewalRequest object
    public static RenewalRequest fromString(String line) {
        String[] parts = line.split(",");
        Long requestId = Long.parseLong(parts[0]);
        String learnerId = parts[1];
        LocalDate requestDate = LocalDate.parse(parts[2]);
        boolean isFirstTime = Boolean.parseBoolean(parts[3]);
        String status = parts[4];
        // Determine the subclass based on isFirstTime
        if (isFirstTime) {
            FirstTimeRenewalRequest request = new FirstTimeRenewalRequest(learnerId, requestDate);
            request.setRequestId(requestId);
            request.setStatus(status);
            return request;
        } else {
            ReturningRenewalRequest request = new ReturningRenewalRequest(learnerId, requestDate);
            request.setRequestId(requestId);
            request.setStatus(status);
            return request;
        }
    }
}
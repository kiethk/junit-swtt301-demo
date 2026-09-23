package com.rtms.backend.dto;

public class ReviewRaceRegistrationRequest {
    private String status;
    private String managerFeedback;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerFeedback() {
        return managerFeedback;
    }

    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }
}

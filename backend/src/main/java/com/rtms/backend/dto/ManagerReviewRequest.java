package com.rtms.backend.dto;

import com.rtms.backend.enums.ReviewDecision;

public class ManagerReviewRequest {

    private ReviewDecision decision;

    private String feedback;

    private Long stallId;

    public ReviewDecision getDecision() {
        return decision;
    }

    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Long getStallId() {
        return stallId;
    }

    public void setStallId(Long stallId) {
        this.stallId = stallId;
    }
}
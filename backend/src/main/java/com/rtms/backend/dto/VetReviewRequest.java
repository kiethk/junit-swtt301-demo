package com.rtms.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rtms.backend.enums.ReviewDecision;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class VetReviewRequest {

    @NotNull(message = "Decision is required")
    private ReviewDecision decision;

    private String feedback;

    @NotNull(message = "Physical exam confirmation is required")
    @AssertTrue(message = "Physical exam must be confirmed")
    private Boolean physicalExamConfirmed;

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

    public Boolean getPhysicalExamConfirmed() {
        return physicalExamConfirmed;
    }

    public void setPhysicalExamConfirmed(Boolean physicalExamConfirmed) {
        this.physicalExamConfirmed = physicalExamConfirmed;
    }

    @JsonIgnore
    @AssertTrue(message = "Feedback is required when decision is REJECTED")
    public boolean isFeedbackValid() {
        return decision != ReviewDecision.REJECTED || (feedback != null && !feedback.isBlank());
    }
}

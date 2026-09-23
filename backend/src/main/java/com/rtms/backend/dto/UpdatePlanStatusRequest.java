package com.rtms.backend.dto;

import com.rtms.backend.enums.TrainingPlanStatus;

public class UpdatePlanStatusRequest {
    private TrainingPlanStatus status;

    public TrainingPlanStatus getStatus() { return status; }
    public void setStatus(TrainingPlanStatus status) { this.status = status; }
}
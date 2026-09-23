package com.rtms.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompletePreventiveCareScheduleRequest {
    private Long veterinarianId;
    private LocalDateTime performedAt;
    private String productOrService;
    private String result;
    private String notes;
    private LocalDate nextDueDate;

    public Long getVeterinarianId() {
        return veterinarianId;
    }

    public void setVeterinarianId(Long veterinarianId) {
        this.veterinarianId = veterinarianId;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public String getProductOrService() {
        return productOrService;
    }

    public void setProductOrService(String productOrService) {
        this.productOrService = productOrService;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}

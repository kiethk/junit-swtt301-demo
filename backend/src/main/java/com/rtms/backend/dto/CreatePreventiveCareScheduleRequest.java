package com.rtms.backend.dto;

import java.time.LocalDate;

public class CreatePreventiveCareScheduleRequest {
    private Long horseId;
    private Long veterinarianId;
    private String careType;
    private LocalDate scheduledDate;
    private String description;

    // Không có: id, status (mặc định SCHEDULED), createdAt, updatedAt

    public Long getHorseId() { return horseId; }
    public void setHorseId(Long horseId) { this.horseId = horseId; }

    public Long getVeterinarianId() { return veterinarianId; }
    public void setVeterinarianId(Long veterinarianId) { this.veterinarianId = veterinarianId; }

    public String getCareType() { return careType; }
    public void setCareType(String careType) { this.careType = careType; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
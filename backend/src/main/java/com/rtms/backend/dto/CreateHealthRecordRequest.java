package com.rtms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateHealthRecordRequest {
    @NotNull
    private Long horseId;

    @NotNull
    private LocalDateTime examinedAt;

    private String symptoms;
    private String findings;
    private String diagnosis;
    private String recordType = "ILLNESS";
    private Long preventiveCareScheduleId;
    private String productOrService;

    private String notes;
    private LocalDate followUpDate;
    private Long sourceTrainingWorkoutId;

    public Long getHorseId() {
        return horseId;
    }

    public void setHorseId(Long horseId) {
        this.horseId = horseId;
    }

    public LocalDateTime getExaminedAt() {
        return examinedAt;
    }

    public void setExaminedAt(LocalDateTime examinedAt) {
        this.examinedAt = examinedAt;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public Long getSourceTrainingWorkoutId() {
        return sourceTrainingWorkoutId;
    }

    public void setSourceTrainingWorkoutId(Long sourceTrainingWorkoutId) {
        this.sourceTrainingWorkoutId = sourceTrainingWorkoutId;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Long getPreventiveCareScheduleId() {
        return preventiveCareScheduleId;
    }

    public void setPreventiveCareScheduleId(Long preventiveCareScheduleId) {
        this.preventiveCareScheduleId = preventiveCareScheduleId;
    }

    public String getProductOrService() {
        return productOrService;
    }

    public void setProductOrService(String productOrService) {
        this.productOrService = productOrService;
    }
}

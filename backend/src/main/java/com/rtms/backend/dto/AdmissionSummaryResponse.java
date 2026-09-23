package com.rtms.backend.dto;

import com.rtms.backend.enums.AdmissionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdmissionSummaryResponse {

    private Long admissionId;
    private AdmissionStatus status;
    private String candidateName;
    private String breed;
    private LocalDate dateOfBirth;
    private LocalDateTime submittedAt;
    private Long quarantineStallId;

    public AdmissionSummaryResponse(
            Long admissionId,
            AdmissionStatus status,
            String candidateName,
            String breed,
            LocalDate dateOfBirth,
            LocalDateTime submittedAt,
            Long quarantineStallId) {
        this.admissionId = admissionId;
        this.status = status;
        this.candidateName = candidateName;
        this.breed = breed;
        this.dateOfBirth = dateOfBirth;
        this.submittedAt = submittedAt;
        this.quarantineStallId = quarantineStallId;
    }

    public Long getAdmissionId() {
        return admissionId;
    }

    public AdmissionStatus getStatus() {
        return status;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getBreed() {
        return breed;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Long getQuarantineStallId() {
        return quarantineStallId;
    }
}
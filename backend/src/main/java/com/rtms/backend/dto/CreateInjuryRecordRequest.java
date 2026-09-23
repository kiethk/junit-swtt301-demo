package com.rtms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateInjuryRecordRequest {
    @NotNull
    private Long healthRecordId;

    @NotNull
    private Long bodyRegionId;

    @NotBlank
    private String injuryType;

    @NotBlank
    private String severity;

    private String description;

    @NotNull
    private LocalDateTime diagnosedAt;

    private LocalDate expectedRecoveryDate;
    private BigDecimal positionX;
    private BigDecimal positionY;
    private BigDecimal positionZ;

    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public Long getBodyRegionId() {
        return bodyRegionId;
    }

    public void setBodyRegionId(Long bodyRegionId) {
        this.bodyRegionId = bodyRegionId;
    }

    public String getInjuryType() {
        return injuryType;
    }

    public void setInjuryType(String injuryType) {
        this.injuryType = injuryType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDiagnosedAt() {
        return diagnosedAt;
    }

    public void setDiagnosedAt(LocalDateTime diagnosedAt) {
        this.diagnosedAt = diagnosedAt;
    }

    public LocalDate getExpectedRecoveryDate() {
        return expectedRecoveryDate;
    }

    public void setExpectedRecoveryDate(LocalDate expectedRecoveryDate) {
        this.expectedRecoveryDate = expectedRecoveryDate;
    }

    public BigDecimal getPositionX() {
        return positionX;
    }

    public void setPositionX(BigDecimal positionX) {
        this.positionX = positionX;
    }

    public BigDecimal getPositionY() {
        return positionY;
    }

    public void setPositionY(BigDecimal positionY) {
        this.positionY = positionY;
    }

    public BigDecimal getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(BigDecimal positionZ) {
        this.positionZ = positionZ;
    }
}

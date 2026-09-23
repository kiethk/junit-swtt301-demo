package com.rtms.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class TreatmentPlanResponse {
    private Long id;
    private Long healthRecordId;
    private String treatmentName;
    private String instructions;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    private List<PrescriptionResponse> prescriptions;

    public TreatmentPlanResponse(Long id, Long healthRecordId, String treatmentName, String instructions, LocalDate startDate, LocalDate endDate, String status, List<PrescriptionResponse> prescriptions) {
        this.id = id;
        this.healthRecordId = healthRecordId;
        this.treatmentName = treatmentName;
        this.instructions = instructions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.prescriptions = prescriptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PrescriptionResponse> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<PrescriptionResponse> prescriptions) {
        this.prescriptions = prescriptions;
    }
}

package com.rtms.backend.dto;

import com.rtms.backend.entity.AdmissionDocument;
import com.rtms.backend.entity.CandidateHorseProfile;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.ReviewDecision;

import java.time.LocalDateTime;
import java.util.List;

public class AdmissionDetailResponse {

    private Long admissionId;
    private Long ownerId;
    private AdmissionStatus status;
    private Long quarantineStallId;
    private String quarantineStallCode;

    private CandidateHorseProfile candidate;
    private List<AdmissionDocument> documents;

    private Long groomId;
    private ReviewDecision groomDecision;
    private String groomFeedback;
    private LocalDateTime groomReviewedAt;

    private Long veterinarianId;
    private ReviewDecision vetDecision;
    private String vetFeedback;
    private LocalDateTime vetReviewedAt;

    private Long trainerId;
    private String trainerFeedback;
    private LocalDateTime trainerReviewedAt;

    private Long managerId;
    private ReviewDecision managerDecision;
    private String managerFeedback;
    private LocalDateTime managerReviewedAt;

    private Long horseId;
    private LocalDateTime submittedAt;

    public Long getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(Long admissionId) {
        this.admissionId = admissionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public AdmissionStatus getStatus() {
        return status;
    }

    public void setStatus(AdmissionStatus status) {
        this.status = status;
    }

    public Long getQuarantineStallId() {
        return quarantineStallId;
    }

    public void setQuarantineStallId(Long quarantineStallId) {
        this.quarantineStallId = quarantineStallId;
    }

    public String getQuarantineStallCode() {
        return quarantineStallCode;
    }

    public void setQuarantineStallCode(String quarantineStallCode) {
        this.quarantineStallCode = quarantineStallCode;
    }

    public CandidateHorseProfile getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateHorseProfile candidate) {
        this.candidate = candidate;
    }

    public List<AdmissionDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<AdmissionDocument> documents) {
        this.documents = documents;
    }

    public Long getGroomId() {
        return groomId;
    }

    public void setGroomId(Long groomId) {
        this.groomId = groomId;
    }

    public ReviewDecision getGroomDecision() {
        return groomDecision;
    }

    public void setGroomDecision(ReviewDecision groomDecision) {
        this.groomDecision = groomDecision;
    }

    public String getGroomFeedback() {
        return groomFeedback;
    }

    public void setGroomFeedback(String groomFeedback) {
        this.groomFeedback = groomFeedback;
    }

    public LocalDateTime getGroomReviewedAt() {
        return groomReviewedAt;
    }

    public void setGroomReviewedAt(LocalDateTime groomReviewedAt) {
        this.groomReviewedAt = groomReviewedAt;
    }

    public Long getVeterinarianId() {
        return veterinarianId;
    }

    public void setVeterinarianId(Long veterinarianId) {
        this.veterinarianId = veterinarianId;
    }

    public ReviewDecision getVetDecision() {
        return vetDecision;
    }

    public void setVetDecision(ReviewDecision vetDecision) {
        this.vetDecision = vetDecision;
    }

    public String getVetFeedback() {
        return vetFeedback;
    }

    public void setVetFeedback(String vetFeedback) {
        this.vetFeedback = vetFeedback;
    }

    public LocalDateTime getVetReviewedAt() {
        return vetReviewedAt;
    }

    public void setVetReviewedAt(LocalDateTime vetReviewedAt) {
        this.vetReviewedAt = vetReviewedAt;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainerFeedback() {
        return trainerFeedback;
    }

    public void setTrainerFeedback(String trainerFeedback) {
        this.trainerFeedback = trainerFeedback;
    }

    public LocalDateTime getTrainerReviewedAt() {
        return trainerReviewedAt;
    }

    public void setTrainerReviewedAt(LocalDateTime trainerReviewedAt) {
        this.trainerReviewedAt = trainerReviewedAt;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public ReviewDecision getManagerDecision() {
        return managerDecision;
    }

    public void setManagerDecision(ReviewDecision managerDecision) {
        this.managerDecision = managerDecision;
    }

    public String getManagerFeedback() {
        return managerFeedback;
    }

    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }

    public LocalDateTime getManagerReviewedAt() {
        return managerReviewedAt;
    }

    public void setManagerReviewedAt(LocalDateTime managerReviewedAt) {
        this.managerReviewedAt = managerReviewedAt;
    }

    public Long getHorseId() {
        return horseId;
    }

    public void setHorseId(Long horseId) {
        this.horseId = horseId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}

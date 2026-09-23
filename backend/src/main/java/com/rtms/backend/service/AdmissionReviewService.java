package com.rtms.backend.service;

import com.rtms.backend.dto.VetReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.ReviewDecision;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AdmissionReviewService {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final StableStallRepository stableStallRepository;

    public AdmissionReviewService(
            AdmissionApplicationRepository admissionApplicationRepository,
            StableStallRepository stableStallRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.stableStallRepository = stableStallRepository;
    }

    @Transactional
    public AdmissionApplication reviewByVet(Long admissionId, VetReviewRequest request, Long actorId) {
        AdmissionApplication admission = admissionApplicationRepository.findByIdForUpdate(admissionId)
                .orElseThrow(() -> new RuntimeException(
                        "Admission application not found with id: " + admissionId));

        if (admission.getStatus() != AdmissionStatus.VET_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission application is not awaiting vet review");
        }
        if (admission.getQuarantineStallId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission application has no quarantine stall");
        }

        ReviewDecision decision = request.getDecision();
        if (decision != ReviewDecision.APPROVED && decision != ReviewDecision.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Decision must be APPROVED or REJECTED");
        }
        if (decision == ReviewDecision.REJECTED
                && (request.getFeedback() == null || request.getFeedback().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feedback is required when decision is REJECTED");
        }
        if (!Boolean.TRUE.equals(request.getPhysicalExamConfirmed())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Physical exam must be confirmed");
        }

        Long assignedVetId = admission.getVeterinarianId();
        if (assignedVetId == null) {
            admission.setVeterinarianId(actorId);
        } else if (!assignedVetId.equals(actorId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the assigned veterinarian can review this admission");
        }

        LocalDateTime now = LocalDateTime.now();
        admission.setVetDecision(decision);
        admission.setVetFeedback(request.getFeedback());
        admission.setVetReviewedAt(now);

        if (decision == ReviewDecision.REJECTED) {
            StableStall stall = stableStallRepository.findById(admission.getQuarantineStallId())
                    .orElseThrow(() -> new RuntimeException(
                            "Stable stall not found with id: " + admission.getQuarantineStallId()));
            stall.setStatus(StallStatus.AVAILABLE);
            admission.setStatus(AdmissionStatus.REJECTED);
        } else {
            admission.setStatus(AdmissionStatus.TRAINER_REVIEW);
        }
        return admission;
    }
}

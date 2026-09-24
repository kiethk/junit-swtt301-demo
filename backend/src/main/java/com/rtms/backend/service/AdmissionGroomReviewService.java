package com.rtms.backend.service;

import com.rtms.backend.dto.GroomReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.ReviewDecision;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdmissionGroomReviewService {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final StableStallRepository stableStallRepository;

    public AdmissionGroomReviewService(
            AdmissionApplicationRepository admissionApplicationRepository,
            StableStallRepository stableStallRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.stableStallRepository = stableStallRepository;
    }

    @Transactional
    public AdmissionApplication review(
            Long admissionId,
            Long groomId,
            GroomReviewRequest request) {

        AdmissionApplication admission = admissionApplicationRepository
                .findByIdForUpdate(admissionId)
                .orElseThrow(() -> new IllegalArgumentException("Admission not found"));

        if (admission.getStatus() != AdmissionStatus.GROOM_REVIEW) {
            throw new IllegalStateException("Admission is not ready for groom review");
        }

        if (request.getDecision() == null) {
            throw new IllegalArgumentException("Decision is required");
        }

        admission.setGroomId(groomId);
        admission.setGroomDecision(request.getDecision());
        admission.setGroomFeedback(request.getFeedback());
        admission.setGroomReviewedAt(LocalDateTime.now());

        if (request.getDecision() == ReviewDecision.REJECTED) {
            requireFeedback(request.getFeedback());
            admission.setStatus(AdmissionStatus.REJECTED);
            return admissionApplicationRepository.save(admission);
        }

        if (request.getDecision() == ReviewDecision.APPROVED) {
            allocateIfCapacityAllows(admission);
            return admissionApplicationRepository.save(admission);
        }

        throw new IllegalArgumentException("Unsupported review decision");
    }

    @Transactional
    public AdmissionApplication allocateWaitingAdmission(Long admissionId) {
        AdmissionApplication admission = admissionApplicationRepository
                .findByIdForUpdate(admissionId)
                .orElseThrow(() -> new IllegalArgumentException("Admission not found"));

        if (admission.getStatus() != AdmissionStatus.WAITING_FOR_STALL) {
            throw new IllegalStateException("Admission is not waiting for stall allocation");
        }

        if (admission.getGroomDecision() != ReviewDecision.APPROVED) {
            throw new IllegalStateException("Admission must be approved by Groom before allocation");
        }

        if (!hasAdmissionCapacity()) {
            return admission;
        }

        assignQuarantineStallAndMoveToVetReview(admission);
        return admissionApplicationRepository.save(admission);
    }

    private void allocateIfCapacityAllows(AdmissionApplication admission) {
        if (!hasAdmissionCapacity()) {
            admission.setStatus(AdmissionStatus.WAITING_FOR_STALL);
            return;
        }

        assignQuarantineStallAndMoveToVetReview(admission);
    }

    private void assignQuarantineStallAndMoveToVetReview(
            AdmissionApplication admission) {
        StableStall quarantineStall = stableStallRepository
                .findFirstAvailableQuarantineStallForUpdate()
                .orElse(null);

        if (quarantineStall == null) {
            admission.setStatus(AdmissionStatus.WAITING_FOR_STALL);
            return;
        }

        quarantineStall.setStatus(StallStatus.OCCUPIED);
        stableStallRepository.save(quarantineStall);

        admission.setQuarantineStallId(quarantineStall.getId());
        admission.setStatus(AdmissionStatus.VET_REVIEW);
    }

    private boolean hasAdmissionCapacity() {
        stableStallRepository.lockAdmissionCapacityStalls();

        long availableQuarantineStalls = stableStallRepository.countByAreaTypeAndStatus(
                "QUARANTINE",
                StallStatus.AVAILABLE.name());
        long occupiedQuarantineStalls = stableStallRepository.countByAreaTypeAndStatus(
                "QUARANTINE",
                StallStatus.OCCUPIED.name());
        long availableRegularStalls = stableStallRepository.countByAreaTypeAndStatus(
                "REGULAR",
                StallStatus.AVAILABLE.name());

        return availableQuarantineStalls >= 1
                && availableRegularStalls >= occupiedQuarantineStalls + 1;
    }

    private void requireFeedback(String feedback) {
        if (feedback == null || feedback.isBlank()) {
            throw new IllegalArgumentException(
                    "Feedback is required when rejecting an admission");
        }
    }
}

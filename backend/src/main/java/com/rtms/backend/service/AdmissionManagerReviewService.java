package com.rtms.backend.service;

import com.rtms.backend.dto.ManagerReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.CandidateHorseProfile;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.HorsePedigree;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.HorseStatus;
import com.rtms.backend.enums.ReviewDecision;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.CandidateHorseProfileRepository;
import com.rtms.backend.repository.HorsePedigreeRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdmissionManagerReviewService {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final CandidateHorseProfileRepository candidateHorseProfileRepository;
    private final StableStallRepository stableStallRepository;
    private final HorseRepository horseRepository;
    private final HorsePedigreeRepository horsePedigreeRepository;

    public AdmissionManagerReviewService(
            AdmissionApplicationRepository admissionApplicationRepository,
            CandidateHorseProfileRepository candidateHorseProfileRepository,
            StableStallRepository stableStallRepository,
            HorseRepository horseRepository,
            HorsePedigreeRepository horsePedigreeRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.candidateHorseProfileRepository = candidateHorseProfileRepository;
        this.stableStallRepository = stableStallRepository;
        this.horseRepository = horseRepository;
        this.horsePedigreeRepository = horsePedigreeRepository;
    }

    @Transactional
    public AdmissionApplication review(
            Long admissionId,
            Long managerId,
            ManagerReviewRequest request) {

        AdmissionApplication admission = admissionApplicationRepository
                .findByIdForUpdate(admissionId)
                .orElseThrow(() -> new IllegalArgumentException("Admission not found"));

        if (admission.getStatus() != AdmissionStatus.MANAGER_REVIEW) {
            throw new IllegalStateException(
                    "Admission is not ready for manager review");
        }

        if (request.getDecision() == null) {
            throw new IllegalArgumentException("Decision is required");
        }

        if (request.getDecision() == ReviewDecision.REJECTED) {
            return reject(admission, managerId, request);
        }

        if (request.getDecision() == ReviewDecision.APPROVED) {
            return approve(admission, managerId, request);
        }

        throw new IllegalArgumentException("Unsupported review decision");
    }

    private AdmissionApplication approve(
            AdmissionApplication admission,
            Long managerId,
            ManagerReviewRequest request) {

        // 1. Lock REGULAR stall
        StableStall regularStall;

        if (request.getStallId() != null) {
            regularStall = stableStallRepository
                    .findAvailableRegularStallByIdForUpdate(request.getStallId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Selected regular stall is no longer available"));
        } else {
            regularStall = stableStallRepository
                    .findFirstAvailableRegularStallForUpdate()
                    .orElseThrow(() -> new IllegalStateException(
                            "No available regular stall"));
        }

        CandidateHorseProfile candidate = candidateHorseProfileRepository
                .findByAdmissionId(admission.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Candidate horse profile not found"));

        // 2. Create Horse
        Horse horse = new Horse();
        horse.setName(candidate.getName());
        horse.setBreed(candidate.getBreed());
        horse.setDateOfBirth(candidate.getDateOfBirth());
        horse.setOwnerId(admission.getOwnerId());
        horse.setCurrentStatus(HorseStatus.ELIGIBLE);
        horse.setCurrentStallId(regularStall.getId());

        horse.setRegistryName(candidate.getRegistryName());
        horse.setRegistrationNumber(candidate.getRegistrationNumber());

        horse = horseRepository.save(horse);

        // 3. Create Pedigree
        HorsePedigree pedigree = new HorsePedigree();
        pedigree.setHorseId(horse.getId());
        pedigree.setPedigreeNotes(candidate.getPedigreeNotes());

        pedigree.setSireName(candidate.getSireName());
        pedigree.setSireRegistrationNumber(
                candidate.getSireRegistrationNumber());

        pedigree.setDamName(candidate.getDamName());
        pedigree.setDamRegistrationNumber(
                candidate.getDamRegistrationNumber());

        pedigree.setSireId(resolveParentHorseId(
                candidate.getSireRegistrationNumber()));

        pedigree.setDamId(resolveParentHorseId(
                candidate.getDamRegistrationNumber()));

        horsePedigreeRepository.save(pedigree);

        // Mark regular stall occupied
        regularStall.setStatus(StallStatus.OCCUPIED);
        stableStallRepository.save(regularStall);

        // 4. Update Admission
        admission.setManagerId(managerId);
        admission.setManagerDecision(ReviewDecision.APPROVED);
        admission.setManagerFeedback(request.getFeedback());
        admission.setManagerReviewedAt(LocalDateTime.now());
        admission.setHorseId(horse.getId());
        admission.setStatus(AdmissionStatus.APPROVED);

        // 5. Release quarantine stall
        Long quarantineStallId = admission.getQuarantineStallId();

        if (quarantineStallId != null) {
            StableStall quarantineStall = stableStallRepository
                    .findById(quarantineStallId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Quarantine stall not found"));

            quarantineStall.setStatus(StallStatus.AVAILABLE);
            stableStallRepository.save(quarantineStall);
        }

        return admissionApplicationRepository.save(admission);
    }

    private Long resolveParentHorseId(String registrationNumber) {

        if (registrationNumber == null
                || registrationNumber.isBlank()) {
            return null;
        }

        List<Horse> matches =
                horseRepository
                        .findByRegistrationNumber(registrationNumber);

        if (matches.size() == 1) {
            return matches.get(0).getId();
        }

        return null;
    }

    private AdmissionApplication reject(
            AdmissionApplication admission,
            Long managerId,
            ManagerReviewRequest request) {

        if (request.getFeedback() == null
                || request.getFeedback().isBlank()) {
            throw new IllegalArgumentException(
                    "Feedback is required when rejecting an admission");
        }

        Long quarantineStallId = admission.getQuarantineStallId();

        if (quarantineStallId != null) {
            StableStall quarantineStall = stableStallRepository
                    .findById(quarantineStallId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Quarantine stall not found"));

            quarantineStall.setStatus(StallStatus.AVAILABLE);
            stableStallRepository.save(quarantineStall);
        }

        admission.setManagerId(managerId);
        admission.setManagerDecision(ReviewDecision.REJECTED);
        admission.setManagerFeedback(request.getFeedback());
        admission.setManagerReviewedAt(LocalDateTime.now());
        admission.setStatus(AdmissionStatus.REJECTED);

        return admissionApplicationRepository.save(admission);
    }
}
package com.rtms.backend.service;

import com.rtms.backend.dto.AdmissionDetailResponse;
import com.rtms.backend.dto.AdmissionSummaryResponse;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.AdmissionDocument;
import com.rtms.backend.entity.CandidateHorseProfile;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.AdmissionDocumentRepository;
import com.rtms.backend.repository.CandidateHorseProfileRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdmissionQueryService {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final CandidateHorseProfileRepository candidateHorseProfileRepository;
    private final AdmissionDocumentRepository admissionDocumentRepository;
    private final StableStallRepository stableStallRepository;

    public AdmissionQueryService(
            AdmissionApplicationRepository admissionApplicationRepository,
            CandidateHorseProfileRepository candidateHorseProfileRepository,
            AdmissionDocumentRepository admissionDocumentRepository,
            StableStallRepository stableStallRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.candidateHorseProfileRepository = candidateHorseProfileRepository;
        this.admissionDocumentRepository = admissionDocumentRepository;
        this.stableStallRepository = stableStallRepository;
    }

    public List<AdmissionSummaryResponse> getAdmissions(AdmissionStatus status) {

        List<AdmissionApplication> admissions;

        if (status != null) {
            admissions = admissionApplicationRepository.findByStatus(status);
        } else {
            admissions = admissionApplicationRepository.findAll();
        }

        return admissions.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public AdmissionDetailResponse getAdmissionDetail(Long admissionId) {

        AdmissionApplication admission = admissionApplicationRepository
                .findById(admissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Admission not found"));

        CandidateHorseProfile candidate = candidateHorseProfileRepository
                .findByAdmissionId(admissionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Candidate horse profile not found"));

        List<AdmissionDocument> documents =
                admissionDocumentRepository.findByAdmissionId(admissionId);

        AdmissionDetailResponse response = new AdmissionDetailResponse();

        response.setAdmissionId(admission.getId());
        response.setOwnerId(admission.getOwnerId());
        response.setStatus(admission.getStatus());
        response.setQuarantineStallId(admission.getQuarantineStallId());

        response.setCandidate(candidate);
        response.setDocuments(documents);

        response.setGroomId(admission.getGroomId());
        response.setGroomDecision(admission.getGroomDecision());
        response.setGroomFeedback(admission.getGroomFeedback());
        response.setGroomReviewedAt(admission.getGroomReviewedAt());

        response.setVeterinarianId(admission.getVeterinarianId());
        response.setVetDecision(admission.getVetDecision());
        response.setVetFeedback(admission.getVetFeedback());
        response.setVetReviewedAt(admission.getVetReviewedAt());

        response.setTrainerId(admission.getTrainerId());

        response.setTrainerFeedback(admission.getTrainerFeedback());
        response.setTrainerReviewedAt(admission.getTrainerReviewedAt());

        response.setManagerId(admission.getManagerId());
        response.setManagerDecision(admission.getManagerDecision());
        response.setManagerFeedback(admission.getManagerFeedback());
        response.setManagerReviewedAt(admission.getManagerReviewedAt());

        response.setHorseId(admission.getHorseId());
        response.setSubmittedAt(admission.getSubmittedAt());

        response.setQuarantineStallCode(admission.getQuarantineStallId() == null
                ? null
                : stableStallRepository.findById(admission.getQuarantineStallId())
                        .map(StableStall::getStallCode)
                        .orElse(null));

        return response;
    }

    private AdmissionSummaryResponse toSummaryResponse(
            AdmissionApplication admission) {

        CandidateHorseProfile candidate = candidateHorseProfileRepository
                .findByAdmissionId(admission.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Candidate horse profile not found"));

        return new AdmissionSummaryResponse(
                admission.getId(),
                admission.getStatus(),
                candidate.getName(),
                candidate.getBreed(),
                candidate.getDateOfBirth(),
                admission.getSubmittedAt(),
                admission.getQuarantineStallId()
        );
    }
}

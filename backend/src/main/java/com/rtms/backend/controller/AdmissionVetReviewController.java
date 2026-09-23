package com.rtms.backend.controller;

import com.rtms.backend.dto.AdmissionDetailResponse;
import com.rtms.backend.dto.AdmissionDocumentResponse;
import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.VetReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.AdmissionDocument;
import com.rtms.backend.entity.CandidateHorseProfile;
import com.rtms.backend.enums.AdmissionDocumentType;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.AdmissionDocumentRepository;
import com.rtms.backend.repository.CandidateHorseProfileRepository;
import com.rtms.backend.repository.StableStallRepository;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.AdmissionReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionVetReviewController {

    private static final Set<AdmissionDocumentType> MEDICAL_DOCUMENT_TYPES = EnumSet.of(
            AdmissionDocumentType.VACCINATION_RECORD,
            AdmissionDocumentType.DEWORMING_RECORD,
            AdmissionDocumentType.HEALTH_CERTIFICATE,
            AdmissionDocumentType.PREVIOUS_MEDICAL_RECORD,
            AdmissionDocumentType.PREVIOUS_INJURY_RECORD);

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final CandidateHorseProfileRepository candidateHorseProfileRepository;
    private final AdmissionDocumentRepository admissionDocumentRepository;
    private final StableStallRepository stableStallRepository;
    private final AdmissionReviewService admissionReviewService;

    public AdmissionVetReviewController(
            AdmissionApplicationRepository admissionApplicationRepository,
            CandidateHorseProfileRepository candidateHorseProfileRepository,
            AdmissionDocumentRepository admissionDocumentRepository,
            StableStallRepository stableStallRepository,
            AdmissionReviewService admissionReviewService) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.candidateHorseProfileRepository = candidateHorseProfileRepository;
        this.admissionDocumentRepository = admissionDocumentRepository;
        this.stableStallRepository = stableStallRepository;
        this.admissionReviewService = admissionReviewService;
    }

    @PostMapping("/{id}/vet-review")
    @PreAuthorize("hasAuthority('ADMISSION_APPLICATION_VET_REVIEW')")
    public ApiResponse<AdmissionDetailResponse> reviewByVet(
            @PathVariable Long id,
            @Valid @RequestBody VetReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        AdmissionApplication admission = admissionReviewService.reviewByVet(
                id, request, currentUser.getUserId());
        return ApiResponse.success(toDetailResponse(admission, findCandidate(id)));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAuthority('ADMISSION_DOCUMENT_VIEW')")
    public ApiResponse<List<AdmissionDocumentResponse>> getDocuments(@PathVariable Long id) {
        findAdmission(id);
        List<AdmissionDocumentResponse> documents = admissionDocumentRepository.findByAdmissionId(id)
                .stream()
                .map(this::toDocumentResponse)
                .toList();
        return ApiResponse.success(documents);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getReason()));
    }

    private AdmissionDetailResponse toDetailResponse(
            AdmissionApplication admission,
            CandidateHorseProfile candidate) {
        AdmissionDetailResponse response = new AdmissionDetailResponse();

        response.setAdmissionId(admission.getId());
        response.setOwnerId(admission.getOwnerId());
        response.setStatus(admission.getStatus());
        response.setQuarantineStallId(admission.getQuarantineStallId());
        response.setQuarantineStallCode(findStallCode(admission.getQuarantineStallId()));
        response.setCandidate(candidate);

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

        return response;
    }

    private AdmissionDocumentResponse toDocumentResponse(AdmissionDocument document) {
        return new AdmissionDocumentResponse(
                document.getId(),
                document.getDocumentType(),
                document.getFileUrl(),
                document.getRecordDate(),
                document.getNote(),
                document.getUploadedAt(),
                MEDICAL_DOCUMENT_TYPES.contains(document.getDocumentType()));
    }

    private AdmissionApplication findAdmission(Long id) {
        return admissionApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admission application not found with id: " + id));
    }

    private CandidateHorseProfile findCandidate(Long admissionId) {
        return candidateHorseProfileRepository.findByAdmissionId(admissionId)
                .orElseThrow(() -> new RuntimeException(
                        "Candidate horse profile not found for admission id: " + admissionId));
    }

    private String findStallCode(Long stallId) {
        if (stallId == null) {
            return null;
        }
        return stableStallRepository.findById(stallId)
                .map(stall -> stall.getStallCode())
                .orElseThrow(() -> new RuntimeException("Stable stall not found with id: " + stallId));
    }
}

package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.GroomReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.AdmissionGroomReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionGroomReviewController {

    private final AdmissionGroomReviewService admissionGroomReviewService;

    public AdmissionGroomReviewController(
            AdmissionGroomReviewService admissionGroomReviewService) {
        this.admissionGroomReviewService = admissionGroomReviewService;
    }

    @PostMapping("/{id}/groom-review")
    @PreAuthorize("hasAuthority('ADMISSION_APPLICATION_GROOM_REVIEW')")
    public ApiResponse<AdmissionApplication> review(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody GroomReviewRequest request) {

        return ApiResponse.success(
                admissionGroomReviewService.review(
                        id,
                        currentUser.getUserId(),
                        request));
    }

    @PostMapping("/{id}/quarantine-allocation")
    @PreAuthorize("hasAuthority('ADMISSION_APPLICATION_GROOM_REVIEW')")
    public ApiResponse<AdmissionApplication> allocateWaitingAdmission(
            @PathVariable Long id) {

        return ApiResponse.success(
                admissionGroomReviewService.allocateWaitingAdmission(id));
    }
}

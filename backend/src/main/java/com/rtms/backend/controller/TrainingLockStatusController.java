package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.TrainingLockStatusResponse;
import com.rtms.backend.service.InjuryRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horses")
public class TrainingLockStatusController {

    private final InjuryRecordService injuryRecordService;

    public TrainingLockStatusController(InjuryRecordService injuryRecordService) {
        this.injuryRecordService = injuryRecordService;
    }

    @PreAuthorize("hasAuthority('HORSE_TRAINING_LOCK_VIEW')")
    @GetMapping("/{id}/training-lock-status")
    public ApiResponse<TrainingLockStatusResponse> getTrainingLockStatus(@PathVariable Long id) {
        return ApiResponse.success(injuryRecordService.getTrainingLockStatus(id));
    }
}

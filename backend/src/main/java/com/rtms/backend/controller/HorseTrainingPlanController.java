package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateHorseTrainingPlanRequest;
import com.rtms.backend.dto.HorseTrainingPlanDetailResponse;
import com.rtms.backend.dto.UpdatePlanStatusRequest;
import com.rtms.backend.entity.HorseTrainingPlan;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.HorseTrainingPlanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/training-plans")
public class HorseTrainingPlanController {

    private final HorseTrainingPlanService planService;

    public HorseTrainingPlanController(HorseTrainingPlanService planService) {
        this.planService = planService;
    }

    @PreAuthorize("hasAuthority('TRAINING_PLAN_VIEW')")
    @GetMapping
    public ApiResponse<List<HorseTrainingPlan>> getAllPlans(@RequestParam(required = false) Long horseId) {
        if (horseId != null) {
            return ApiResponse.success(planService.getPlansByHorse(horseId));
        }
        return ApiResponse.success(planService.getAllPlans());
    }

    @PreAuthorize("hasAuthority('TRAINING_PLAN_VIEW')")
    @GetMapping("/{id}")
    public ApiResponse<HorseTrainingPlanDetailResponse> getPlanById(@PathVariable Long id) {
        return ApiResponse.success(planService.getPlanById(id));
    }

    @PreAuthorize("hasAuthority('TRAINING_PLAN_CREATE')")
    @PostMapping
    public ApiResponse<HorseTrainingPlanDetailResponse> createPlan(@RequestBody CreateHorseTrainingPlanRequest request) {
        AuthenticatedUser currentUser = (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();
        return ApiResponse.success(planService.createPlan(request, currentUser));
    }

    @PreAuthorize("hasAuthority('TRAINING_PLAN_UPDATE')")
    @PutMapping("/{id}/status")
    public ApiResponse<HorseTrainingPlan> updatePlanStatus(@PathVariable Long id,
                                                           @RequestBody UpdatePlanStatusRequest request) {
        return ApiResponse.success(planService.updatePlanStatus(id, request));
    }
}
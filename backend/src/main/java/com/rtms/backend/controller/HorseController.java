package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateHorseRequest;
import com.rtms.backend.dto.UpdateHorseStatusRequest;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.service.HorseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    public HorseController(HorseService horseService) {
        this.horseService = horseService;
    }

    @PreAuthorize("hasAuthority('HORSE_VIEW')")
    @GetMapping
    public ApiResponse<List<Horse>> getAllHorses() {
        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ApiResponse.success(horseService.getAllHorses(currentUser));
    }

    @PreAuthorize("hasAuthority('HORSE_CREATE')")
    @PostMapping
    public ApiResponse<Horse> createHorse(@RequestBody CreateHorseRequest request) {
        return ApiResponse.success(horseService.createHorse(request));
    }

    @PreAuthorize("hasAuthority('HORSE_VIEW')")
    @GetMapping("/{id}")
    public ApiResponse<Horse> getHorseById(@PathVariable Long id) {
        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ApiResponse.success(horseService.getHorseById(id, currentUser));
    }

    @PreAuthorize("hasAuthority('HORSE_STATUS_EDIT')")
    @PutMapping("/{id}/status")
    public ApiResponse<Horse> updateHorseStatus(@PathVariable Long id,
            @RequestBody UpdateHorseStatusRequest request) {
        return ApiResponse.success(horseService.updateHorseStatus(id, request));
    }

    @PreAuthorize("hasAuthority('STABLE_STALL_UPDATE')")
    @PutMapping("/{id}/assign-stall")
    public ApiResponse<Horse> assignStall(
            @PathVariable Long id,
            @RequestParam Long stallId) {
        return ApiResponse.success(horseService.assignStall(id, stallId));
    }

}

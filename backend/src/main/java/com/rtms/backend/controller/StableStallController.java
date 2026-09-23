package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.entity.Area;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AreaType;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.AreaRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stalls")
public class StableStallController {

    private final StableStallRepository stableStallRepository;
    private final AreaRepository areaRepository;

    public StableStallController(
            StableStallRepository stableStallRepository,
            AreaRepository areaRepository) {
        this.stableStallRepository = stableStallRepository;
        this.areaRepository = areaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STABLE_STALL_VIEW')")
    public ApiResponse<List<StableStall>> getStalls(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) AreaType areaType,
            @RequestParam(required = false) StallStatus status,
            @RequestParam(required = false) Long groomId) {

        if (groomId != null) {
            return ApiResponse.success(stableStallRepository.findByGroomId(groomId));
        }

        if (areaCode != null) {
            Area area = areaRepository.findByCode(areaCode)
                    .orElseThrow(() -> new RuntimeException("Area not found with code: " + areaCode));

            if (status != null) {
                return ApiResponse.success(
                        stableStallRepository.findByAreaIdAndStatus(area.getId(), status));
            }

            return ApiResponse.success(
                    stableStallRepository.findByAreaId(area.getId()));
        }

        if (areaType != null) {
            List<Long> areaIds = areaRepository.findByType(areaType)
                    .stream()
                    .map(Area::getId)
                    .toList();

            List<StableStall> stalls = stableStallRepository.findAll()
                    .stream()
                    .filter(stall -> areaIds.contains(stall.getAreaId()))
                    .filter(stall -> status == null || stall.getStatus() == status)
                    .toList();

            return ApiResponse.success(stalls);
        }

        if (status != null) {
            return ApiResponse.success(
                    stableStallRepository.findByStatus(status));
        }

        return ApiResponse.success(
                stableStallRepository.findAll());
    }

    @PutMapping("/{id}/assign-groom")
    @PreAuthorize("hasAuthority('STABLE_STALL_UPDATE')")
    public ApiResponse<StableStall> assignGroom(
            @PathVariable Long id,
            @RequestParam(required = false) Long groomId) {
        StableStall stall = stableStallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stable stall not found with id: " + id));

        stall.setGroomId(groomId);
        return ApiResponse.success(stableStallRepository.save(stall));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STABLE_STALL_VIEW')")
    public ApiResponse<StableStall> getStallById(@PathVariable Long id) {
        StableStall stall = stableStallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stable stall not found with id: " + id));

        return ApiResponse.success(stall);
    }
}
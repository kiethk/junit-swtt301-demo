package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.entity.Area;
import com.rtms.backend.enums.AreaType;
import com.rtms.backend.repository.AreaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaRepository areaRepository;

    public AreaController(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AREA_VIEW')")
    public ApiResponse<List<Area>> getAreas(
            @RequestParam(required = false) AreaType type
    ) {
        List<Area> areas;

        if (type != null) {
            areas = areaRepository.findByType(type);
        } else {
            areas = areaRepository.findAll();
        }

        return ApiResponse.success(areas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AREA_VIEW')")
    public ApiResponse<Area> getAreaById(@PathVariable Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Area not found with id: " + id));

        return ApiResponse.success(area);
    }
}
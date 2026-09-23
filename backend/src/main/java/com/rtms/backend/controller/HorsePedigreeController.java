package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateHorsePedigreeRequest;
import com.rtms.backend.dto.HorsePedigreeResponse;
import com.rtms.backend.service.HorsePedigreeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horses")
public class HorsePedigreeController {
    private final HorsePedigreeService horsePedigreeService;

    public HorsePedigreeController(HorsePedigreeService horsePedigreeService) {
        this.horsePedigreeService = horsePedigreeService;
    }

    @GetMapping("/{horseId}/pedigree")
    @PreAuthorize("hasAuthority('HORSE_PEDIGREE_VIEW')")
    public ApiResponse<HorsePedigreeResponse> getPedigree(@PathVariable Long horseId) {
        return ApiResponse.success(horsePedigreeService.getByHorseId(horseId));
    }

    @PostMapping("/{horseId}/pedigree")
    @PreAuthorize("hasAuthority('HORSE_PEDIGREE_MANAGE')")
    public ApiResponse<HorsePedigreeResponse> createOrUpdatePedigree(
            @PathVariable Long horseId,
            @Valid @RequestBody CreateHorsePedigreeRequest request
    ) {
        return ApiResponse.success(
                horsePedigreeService.createOrUpdate(
                        horseId,
                        request
                )
        );
    }
}

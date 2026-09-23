package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateInjuryRecordRequest;
import com.rtms.backend.entity.InjuryRecord;
import com.rtms.backend.service.InjuryRecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/injury-records")
public class InjuryRecordController {

    private final InjuryRecordService injuryRecordService;

    public InjuryRecordController(InjuryRecordService injuryRecordService) {
        this.injuryRecordService = injuryRecordService;
    }

    @PreAuthorize("hasAuthority('INJURY_RECORD_CREATE')")
    @PostMapping
    public ApiResponse<InjuryRecord> create(@Valid @RequestBody CreateInjuryRecordRequest request) {
        return ApiResponse.success(injuryRecordService.createInjuryRecord(request));
    }
}

package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateGroomIncidentReportRequest;
import com.rtms.backend.entity.GroomIncidentReport;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.GroomIncidentReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groom-incident-reports")
public class GroomIncidentReportController {

    private final GroomIncidentReportService incidentReportService;

    public GroomIncidentReportController(GroomIncidentReportService incidentReportService) {
        this.incidentReportService = incidentReportService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GROOM_INCIDENT_REPORT_CREATE')")
    public ApiResponse<GroomIncidentReport> createReport(
            @RequestBody CreateGroomIncidentReportRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        GroomIncidentReport created = incidentReportService.createReport(request, currentUser);
        return ApiResponse.success(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GROOM_INCIDENT_REPORT_VIEW')")
    public ApiResponse<List<GroomIncidentReport>> getReports(
            @RequestParam(required = false) Long horseId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<GroomIncidentReport> reports = incidentReportService.getReports(horseId, currentUser);
        return ApiResponse.success(reports);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GROOM_INCIDENT_REPORT_VIEW')")
    public ApiResponse<GroomIncidentReport> getReportById(
            @PathVariable Long id) {
        GroomIncidentReport report = incidentReportService.getReportById(id);
        return ApiResponse.success(report);
    }
}

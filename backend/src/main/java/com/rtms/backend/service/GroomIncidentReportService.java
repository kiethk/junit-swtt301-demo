package com.rtms.backend.service;

import com.rtms.backend.dto.CreateGroomIncidentReportRequest;
import com.rtms.backend.entity.GroomIncidentReport;
import com.rtms.backend.enums.IncidentSeverity;
import com.rtms.backend.repository.GroomIncidentReportRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroomIncidentReportService {

    private final GroomIncidentReportRepository incidentReportRepository;
    private final HorseRepository horseRepository;

    public GroomIncidentReportService(GroomIncidentReportRepository incidentReportRepository,
                                      HorseRepository horseRepository) {
        this.incidentReportRepository = incidentReportRepository;
        this.horseRepository = horseRepository;
    }

    @Transactional
    public GroomIncidentReport createReport(CreateGroomIncidentReportRequest request, AuthenticatedUser currentUser) {
        if (request.getHorseId() == null) {
            throw new RuntimeException("Horse ID is required");
        }
        horseRepository.findById(request.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + request.getHorseId()));

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Description is required");
        }

        IncidentSeverity severity = request.getSeverity() != null ? request.getSeverity() : IncidentSeverity.MEDIUM;

        GroomIncidentReport report = new GroomIncidentReport(
                currentUser.getUserId(),
                request.getHorseId(),
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getImageUrl(),
                severity
        );

        return incidentReportRepository.save(report);
    }

    public List<GroomIncidentReport> getReports(Long horseId, AuthenticatedUser currentUser) {
        if (horseId != null) {
            return incidentReportRepository.findByHorseId(horseId);
        }

        if ("GROOM".equalsIgnoreCase(currentUser.getRole())) {
            return incidentReportRepository.findByGroomId(currentUser.getUserId());
        }

        return incidentReportRepository.findAllByOrderByReportedAtDesc();
    }

    public GroomIncidentReport getReportById(Long id) {
        return incidentReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident report not found with id: " + id));
    }
}

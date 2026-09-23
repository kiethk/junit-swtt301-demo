package com.rtms.backend.repository;

import com.rtms.backend.entity.GroomIncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroomIncidentReportRepository extends JpaRepository<GroomIncidentReport, Long> {

    List<GroomIncidentReport> findByGroomId(Long groomId);

    List<GroomIncidentReport> findByHorseId(Long horseId);

    List<GroomIncidentReport> findAllByOrderByReportedAtDesc();
}

package com.rtms.backend.repository;

import com.rtms.backend.entity.Prescription;
import com.rtms.backend.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
    List<Prescription> findByTreatmentPlanId(Long treatmentPlanId);
}

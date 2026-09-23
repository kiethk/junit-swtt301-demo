package com.rtms.backend.repository;

import com.rtms.backend.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan,Long> {
    List<TreatmentPlan> findByHealthRecordId(Long healthRecordId);
}

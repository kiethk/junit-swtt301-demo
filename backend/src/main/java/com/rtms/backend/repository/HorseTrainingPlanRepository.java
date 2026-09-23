package com.rtms.backend.repository;

import com.rtms.backend.entity.HorseTrainingPlan;
import com.rtms.backend.enums.TrainingPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface HorseTrainingPlanRepository extends JpaRepository<HorseTrainingPlan, Long> {
    List<HorseTrainingPlan> findByHorseId(Long horseId);
    List<HorseTrainingPlan> findByStatus(TrainingPlanStatus status);
    List<HorseTrainingPlan> findByHorseIdAndStatus(Long horseId, TrainingPlanStatus status);
    List<HorseTrainingPlan> findByHorseIdAndStatusInOrderByEndDateDesc(Long horseId, Collection<TrainingPlanStatus> statuses);
}
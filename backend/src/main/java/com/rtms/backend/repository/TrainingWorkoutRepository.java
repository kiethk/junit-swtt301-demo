package com.rtms.backend.repository;

import com.rtms.backend.entity.TrainingWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingWorkoutRepository extends JpaRepository<TrainingWorkout, Long> {
    List<TrainingWorkout> findByPlanIdOrderByWorkoutDateAsc(Long planId);
    List<TrainingWorkout> findByHorseId(Long horseId);

    // Kiểm tra xem Trainer đã có lịch huấn luyện chiến mã nào khác trong khung giờ này chưa
    @Query("SELECT tw FROM TrainingWorkout tw " +
            "JOIN HorseTrainingPlan p ON tw.planId = p.id " +
            "WHERE p.trainerId = :trainerId " +
            "AND tw.status != com.rtms.backend.enums.WorkoutStatus.CANCELLED " +
            "AND tw.startTime < :endTime AND tw.endTime > :startTime")
    List<TrainingWorkout> findConflictingWorkoutsForTrainer(
            @Param("trainerId") Long trainerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
package com.rtms.backend.entity;

import com.rtms.backend.enums.WorkoutStatus;
import com.rtms.backend.enums.WorkoutType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_workouts")
public class TrainingWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "horse_id", nullable = false)
    private Long horseId;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", length = 20)
    private WorkoutType workoutType = WorkoutType.REGULAR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutStatus status = WorkoutStatus.SCHEDULED;

    @Column(name = "actual_distance_meters")
    private BigDecimal actualDistanceMeters;

    @Column(name = "actual_duration_minutes")
    private BigDecimal actualDurationMinutes;

    @Column(name = "top_speed_kmh")
    private BigDecimal topSpeedKmh;

    @Column(name = "average_speed_kmh")
    private BigDecimal averageSpeedKmh;

    @Column(name = "average_heart_rate")
    private Integer averageHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "recovery_heart_rate")
    private Integer recoveryHeartRate;

    @Column(name = "performance_rating")
    private Integer performanceRating;

    @Column(name = "trainer_feedback")
    private String trainerFeedback;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getHorseId() { return horseId; }
    public void setHorseId(Long horseId) { this.horseId = horseId; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public WorkoutType getWorkoutType() { return workoutType; }
    public void setWorkoutType(WorkoutType workoutType) { this.workoutType = workoutType; }

    public WorkoutStatus getStatus() { return status; }
    public void setStatus(WorkoutStatus status) { this.status = status; }

    public BigDecimal getActualDistanceMeters() { return actualDistanceMeters; }
    public void setActualDistanceMeters(BigDecimal actualDistanceMeters) { this.actualDistanceMeters = actualDistanceMeters; }

    public BigDecimal getActualDurationMinutes() { return actualDurationMinutes; }
    public void setActualDurationMinutes(BigDecimal actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; }

    public BigDecimal getTopSpeedKmh() { return topSpeedKmh; }
    public void setTopSpeedKmh(BigDecimal topSpeedKmh) { this.topSpeedKmh = topSpeedKmh; }

    public BigDecimal getAverageSpeedKmh() { return averageSpeedKmh; }
    public void setAverageSpeedKmh(BigDecimal averageSpeedKmh) { this.averageSpeedKmh = averageSpeedKmh; }

    public Integer getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(Integer averageHeartRate) { this.averageHeartRate = averageHeartRate; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getRecoveryHeartRate() { return recoveryHeartRate; }
    public void setRecoveryHeartRate(Integer recoveryHeartRate) { this.recoveryHeartRate = recoveryHeartRate; }

    public Integer getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }

    public String getTrainerFeedback() { return trainerFeedback; }
    public void setTrainerFeedback(String trainerFeedback) { this.trainerFeedback = trainerFeedback; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
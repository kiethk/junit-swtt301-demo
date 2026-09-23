package com.rtms.backend.entity;

import com.rtms.backend.enums.GroomTaskType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "groom_daily_tasks")
public class GroomDailyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "groom_id", nullable = false)
    private Long groomId;

    @Column(name = "horse_id", nullable = false)
    private Long horseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 100)
    private GroomTaskType taskType;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public GroomDailyTask() {
    }

    public GroomDailyTask(Long groomId, Long horseId, GroomTaskType taskType, LocalDateTime scheduledTime, String notes) {
        this.groomId = groomId;
        this.horseId = horseId;
        this.taskType = taskType;
        this.scheduledTime = scheduledTime;
        this.notes = notes;
        this.isCompleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroomId() {
        return groomId;
    }

    public void setGroomId(Long groomId) {
        this.groomId = groomId;
    }

    public Long getHorseId() {
        return horseId;
    }

    public void setHorseId(Long horseId) {
        this.horseId = horseId;
    }

    public GroomTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(GroomTaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

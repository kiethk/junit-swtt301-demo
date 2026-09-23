package com.rtms.backend.dto;

import com.rtms.backend.enums.GroomTaskType;

import java.time.LocalDateTime;

public class CreateGroomDailyTaskRequest {

    private Long groomId;
    private Long horseId;
    private GroomTaskType taskType;
    private LocalDateTime scheduledTime;
    private String notes;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

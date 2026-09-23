package com.rtms.backend.dto;

public class TrainingLockStatusResponse {
    private Long horseId;
    private String currentStatus;
    private boolean locked;

    public TrainingLockStatusResponse(Long horseId, String currentStatus, boolean locked) {
        this.horseId = horseId;
        this.currentStatus = currentStatus;
        this.locked = locked;
    }

    public Long getHorseId() {
        return horseId;
    }

    public void setHorseId(Long horseId) {
        this.horseId = horseId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}

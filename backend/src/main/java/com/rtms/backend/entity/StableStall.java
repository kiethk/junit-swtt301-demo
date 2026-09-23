package com.rtms.backend.entity;

import com.rtms.backend.enums.StallStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stable_stalls", uniqueConstraints = {
        @UniqueConstraint(name = "uq_stable_stalls_area_number", columnNames = { "area_id", "stall_number" })
})
public class StableStall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @Column(name = "groom_id")
    private Long groomId;

    @Column(name = "stall_number", nullable = false)
    private Integer stallNumber;

    @Column(name = "stall_code", nullable = false, unique = true, length = 50)
    private String stallCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StallStatus status = StallStatus.AVAILABLE;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getGroomId() {
        return groomId;
    }

    public void setGroomId(Long groomId) {
        this.groomId = groomId;
    }

    public Integer getStallNumber() {
        return stallNumber;
    }

    public void setStallNumber(Integer stallNumber) {
        this.stallNumber = stallNumber;
    }

    public String getStallCode() {
        return stallCode;
    }

    public void setStallCode(String stallCode) {
        this.stallCode = stallCode;
    }

    public StallStatus getStatus() {
        return status;
    }

    public void setStatus(StallStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
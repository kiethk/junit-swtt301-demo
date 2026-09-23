/**
 * Horse — REFERENCE IMPLEMENTATION
 *
 * Đây là ví dụ mẫu (reference) về cách tạo một JPA Entity trong dự án.
 * Các entity khác (Trainer, Race, Training...) làm theo đúng convention này:
 *  - @Entity + @Table(name = "tên_bảng_snake_case")
 *  - @Column(name = "tên_cột") map rõ ràng giữa Java camelCase và SQL snake_case
 *  - @PrePersist / @PreUpdate để tự động set created_at / updated_at
 *  - Không dùng Lombok — viết getter/setter thủ công để dễ debug và rõ ràng hơn
 *  - Migration SQL tương ứng phải được tạo trong db/migration/ (xem V1__create_horses_table.sql)
 */
package com.rtms.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.rtms.backend.enums.HorseStatus;

@Entity
@Table(name = "horses")
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String breed;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private HorseStatus currentStatus = HorseStatus.ELIGIBLE;

    @Column(name = "stable_location")
    private String stableLocation;

    @Column(name = "current_stall_id", unique = true)
    private Long currentStallId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "registry_name")
    private String registryName;

    @Column(name = "registration_number")
    private String registrationNumber;

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public HorseStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(HorseStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getStableLocation() {
        return stableLocation;
    }

    public void setStableLocation(String stableLocation) {
        this.stableLocation = stableLocation;
    }

    public Long getCurrentStallId() {
        return currentStallId;
    }

    public void setCurrentStallId(Long currentStallId) {
        this.currentStallId = currentStallId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
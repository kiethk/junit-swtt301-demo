package com.rtms.backend.dto;

import java.time.LocalDate;

public class CreateHorseRequest {
    private String name;
    private String breed;
    private LocalDate dateOfBirth;
    private String stableLocation;
    private Long ownerId;

    // Chỉ liệt kê đúng những field mà client ĐƯỢC PHÉP nhập khi tạo mới.
    // Không có: id, currentStatus, createdAt, updatedAt

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getStableLocation() { return stableLocation; }
    public void setStableLocation(String stableLocation) { this.stableLocation = stableLocation; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
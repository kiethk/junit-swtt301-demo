package com.rtms.backend.dto;

import com.rtms.backend.enums.IntensityLevel;
import com.rtms.backend.enums.SurfaceType;

import java.math.BigDecimal;

public class CreateSubjectRequest {
    private Long categoryId;
    private String name;
    private String description;
    private SurfaceType surfaceType;        // TURF, DIRT, SYNTHETIC
    private BigDecimal targetDistanceMeters;
    private IntensityLevel intensityLevel;     // LOW, MEDIUM, HIGH

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SurfaceType getSurfaceType() { return surfaceType; }
    public void setSurfaceType(SurfaceType surfaceType) { this.surfaceType = surfaceType; }

    public BigDecimal getTargetDistanceMeters() { return targetDistanceMeters; }
    public void setTargetDistanceMeters(BigDecimal targetDistanceMeters) { this.targetDistanceMeters = targetDistanceMeters; }

    public IntensityLevel getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }
}
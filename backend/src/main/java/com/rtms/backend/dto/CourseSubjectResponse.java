package com.rtms.backend.dto;

import com.rtms.backend.enums.IntensityLevel;
import com.rtms.backend.enums.SurfaceType;

import java.math.BigDecimal;

public class CourseSubjectResponse {
    private Long id; // ID bản ghi liên kết course_subjects
    private Long subjectId;
    private String subjectName;
    private String description;
    private SurfaceType surfaceType;
    private BigDecimal targetDistanceMeters;
    private IntensityLevel intensityLevel;
    private Integer orderIndex;

    public CourseSubjectResponse(Long id, Long subjectId, String subjectName, String description,
                                 SurfaceType surfaceType, BigDecimal targetDistanceMeters,
                                 IntensityLevel intensityLevel, Integer orderIndex) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.description = description;
        this.surfaceType = surfaceType;
        this.targetDistanceMeters = targetDistanceMeters;
        this.intensityLevel = intensityLevel;
        this.orderIndex = orderIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SurfaceType getSurfaceType() { return surfaceType; }
    public void setSurfaceType(SurfaceType surfaceType) { this.surfaceType = surfaceType; }

    public BigDecimal getTargetDistanceMeters() { return targetDistanceMeters; }
    public void setTargetDistanceMeters(BigDecimal targetDistanceMeters) { this.targetDistanceMeters = targetDistanceMeters; }

    public IntensityLevel getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
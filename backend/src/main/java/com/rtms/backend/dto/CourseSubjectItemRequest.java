package com.rtms.backend.dto;

public class CourseSubjectItemRequest {
    private Long subjectId;
    private Integer orderIndex;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
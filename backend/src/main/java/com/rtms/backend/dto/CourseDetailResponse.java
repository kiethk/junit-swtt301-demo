package com.rtms.backend.dto;

import com.rtms.backend.entity.Course;
import java.util.List;

public class CourseDetailResponse {
    private Course course;
    private List<CourseSubjectResponse> subjects;

    public CourseDetailResponse(Course course, List<CourseSubjectResponse> subjects) {
        this.course = course;
        this.subjects = subjects;
    }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public List<CourseSubjectResponse> getSubjects() { return subjects; }
    public void setSubjects(List<CourseSubjectResponse> subjects) { this.subjects = subjects; }
}
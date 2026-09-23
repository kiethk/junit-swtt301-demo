package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CourseDetailResponse;
import com.rtms.backend.dto.CourseSubjectItemRequest;
import com.rtms.backend.dto.CourseSubjectResponse;
import com.rtms.backend.dto.CreateCourseRequest;
import com.rtms.backend.entity.Course;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasAuthority('COURSE_VIEW')")
    @GetMapping
    public ApiResponse<List<Course>> getAllCourses() {
        return ApiResponse.success(courseService.getAllCourses());
    }

    @PreAuthorize("hasAuthority('COURSE_VIEW')")
    @GetMapping("/{id}")
    public ApiResponse<CourseDetailResponse> getCourseById(@PathVariable Long id) {
        return ApiResponse.success(courseService.getCourseById(id));
    }

    @PreAuthorize("hasAuthority('COURSE_CREATE')")
    @PostMapping
    public ApiResponse<CourseDetailResponse> createCourse(@RequestBody CreateCourseRequest request) {
        AuthenticatedUser currentUser = (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();
        assert currentUser != null;
        return ApiResponse.success(courseService.createCourse(request, currentUser));
    }

    @PreAuthorize("hasAuthority('COURSE_CREATE')")
    @PostMapping("/{id}/subjects")
    public ApiResponse<CourseSubjectResponse> addSubjectToCourse(@PathVariable Long id,
                                                                 @RequestBody CourseSubjectItemRequest request) {
        return ApiResponse.success(courseService.addSubjectToCourse(id, request));
    }
}
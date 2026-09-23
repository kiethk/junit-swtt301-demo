package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateSubjectRequest;
import com.rtms.backend.entity.Subject;
import com.rtms.backend.service.SubjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PreAuthorize("hasAuthority('SUBJECT_VIEW')")
    @GetMapping
    public ApiResponse<List<Subject>> getAllSubjects(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(subjectService.getAllSubjects(categoryId));
    }

    @PreAuthorize("hasAuthority('SUBJECT_VIEW')")
    @GetMapping("/{id}")
    public ApiResponse<Subject> getSubjectById(@PathVariable Long id) {
        return ApiResponse.success(subjectService.getSubjectById(id));
    }

    @PreAuthorize("hasAuthority('SUBJECT_MANAGE')")
    @PostMapping
    public ApiResponse<Subject> createSubject(@RequestBody CreateSubjectRequest request) {
        return ApiResponse.success(subjectService.createSubject(request));
    }
}
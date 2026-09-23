package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.entity.SubjectCategory;
import com.rtms.backend.service.SubjectCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subject-categories")
public class SubjectCategoryController {

    private final SubjectCategoryService categoryService;

    public SubjectCategoryController(SubjectCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasAuthority('SUBJECT_CATEGORY_VIEW')")
    @GetMapping
    public ApiResponse<List<SubjectCategory>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }
}
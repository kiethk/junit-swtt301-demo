package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateGroomDailyTaskRequest;
import com.rtms.backend.entity.GroomDailyTask;
import com.rtms.backend.security.AuthenticatedUser;
import com.rtms.backend.service.GroomDailyTaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/groom-daily-tasks")
public class GroomDailyTaskController {

    private final GroomDailyTaskService taskService;

    public GroomDailyTaskController(GroomDailyTaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GROOM_DAILY_TASK_CREATE')")
    public ApiResponse<GroomDailyTask> createTask(
            @RequestBody CreateGroomDailyTaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        GroomDailyTask created = taskService.createTask(request, currentUser);
        return ApiResponse.success(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GROOM_DAILY_TASK_VIEW')")
    public ApiResponse<List<GroomDailyTask>> getDailyTasks(
            @RequestParam(required = false) Long groomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<GroomDailyTask> tasks = taskService.getDailyTasks(groomId, date, currentUser);
        return ApiResponse.success(tasks);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('GROOM_DAILY_TASK_UPDATE')")
    public ApiResponse<GroomDailyTask> completeTask(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        GroomDailyTask completed = taskService.completeTask(id, currentUser);
        return ApiResponse.success(completed);
    }

    @PostMapping("/generate-routine")
    @PreAuthorize("hasAuthority('GROOM_DAILY_TASK_CREATE')")
    public ApiResponse<List<GroomDailyTask>> generateRoutine(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GroomDailyTask> tasks = taskService.generateDailyRoutineTasks(date);
        return ApiResponse.success(tasks);
    }
}

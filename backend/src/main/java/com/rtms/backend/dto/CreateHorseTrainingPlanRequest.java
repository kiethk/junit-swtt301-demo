package com.rtms.backend.dto;

import com.rtms.backend.enums.TrainingDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CreateHorseTrainingPlanRequest {
    private Long horseId;
    private Long courseId;
    private LocalDate startDate;
    // Danh sách các thứ trong tuần muốn tập: VD ["MONDAY", "WEDNESDAY", "FRIDAY"]
    private List<TrainingDay> trainingDays;

    // Groom bắt buộc phụ trách hỗ trợ & vệ sinh cho buổi tập
    private Long groomId;

    private LocalTime startTime; // Giờ bắt đầu tập (VD: 07:00)
    private LocalTime endTime;   // Giờ kết thúc tập (VD: 08:30)
    private String notes;

    public Long getHorseId() { return horseId; }
    public void setHorseId(Long horseId) { this.horseId = horseId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public List<TrainingDay> getTrainingDays() { return trainingDays; }
    public void setTrainingDays(List<TrainingDay> trainingDays) { this.trainingDays = trainingDays; }

    public Long getGroomId() { return groomId; }
    public void setGroomId(Long groomId) { this.groomId = groomId; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
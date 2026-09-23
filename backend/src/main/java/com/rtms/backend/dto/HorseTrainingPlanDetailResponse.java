package com.rtms.backend.dto;

import com.rtms.backend.entity.HorseTrainingPlan;
import com.rtms.backend.entity.TrainingWorkout;

import java.util.List;

public class HorseTrainingPlanDetailResponse {
    private HorseTrainingPlan plan;
    private List<TrainingWorkout> workouts;

    public HorseTrainingPlanDetailResponse(HorseTrainingPlan plan, List<TrainingWorkout> workouts) {
        this.plan = plan;
        this.workouts = workouts;
    }

    public HorseTrainingPlan getPlan() { return plan; }
    public void setPlan(HorseTrainingPlan plan) { this.plan = plan; }

    public List<TrainingWorkout> getWorkouts() { return workouts; }
    public void setWorkouts(List<TrainingWorkout> workouts) { this.workouts = workouts; }
}
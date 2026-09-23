package com.rtms.backend.service;

import com.rtms.backend.dto.CreateHorseTrainingPlanRequest;
import com.rtms.backend.dto.HorseTrainingPlanDetailResponse;
import com.rtms.backend.dto.TrainingLockStatusResponse;
import com.rtms.backend.dto.UpdatePlanStatusRequest;
import com.rtms.backend.entity.*;
import com.rtms.backend.enums.TrainingDay;
import com.rtms.backend.enums.TrainingPlanStatus;
import com.rtms.backend.enums.WorkoutStatus;
import com.rtms.backend.enums.WorkoutType;
import com.rtms.backend.repository.CourseRepository;
import com.rtms.backend.repository.CourseSubjectRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.HorseTrainingPlanRepository;
import com.rtms.backend.repository.SubjectRepository;
import com.rtms.backend.repository.TrainingWorkoutRepository;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HorseTrainingPlanService {

    private final HorseTrainingPlanRepository planRepository;
    private final TrainingWorkoutRepository workoutRepository;
    private final CourseRepository courseRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final HorseRepository horseRepository;
    private final InjuryRecordService injuryRecordService;

    public HorseTrainingPlanService(HorseTrainingPlanRepository planRepository,
                                    TrainingWorkoutRepository workoutRepository,
                                    CourseRepository courseRepository,
                                    CourseSubjectRepository courseSubjectRepository,
                                    SubjectRepository subjectRepository,
                                    HorseRepository horseRepository,
                                    InjuryRecordService injuryRecordService) {
        this.planRepository = planRepository;
        this.workoutRepository = workoutRepository;
        this.courseRepository = courseRepository;
        this.courseSubjectRepository = courseSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.horseRepository = horseRepository;
        this.injuryRecordService = injuryRecordService;
    }

    public List<HorseTrainingPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<HorseTrainingPlan> getPlansByHorse(Long horseId) {
        return planRepository.findByHorseId(horseId);
    }

    public HorseTrainingPlanDetailResponse getPlanById(Long id) {
        HorseTrainingPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training plan not found with id: " + id));
        List<TrainingWorkout> workouts = workoutRepository.findByPlanIdOrderByWorkoutDateAsc(id);
        return new HorseTrainingPlanDetailResponse(plan, workouts);
    }

    @Transactional
    public HorseTrainingPlanDetailResponse createPlan(CreateHorseTrainingPlanRequest request, AuthenticatedUser currentUser) {
        // 0. KIỂM TRA BẮT BUỘC GROOM VÀ KHUNG GIỜ
        if (request.getGroomId() == null) {
            throw new IllegalArgumentException("Bắt buộc phải chỉ định Groom phụ trách hỗ trợ buổi tập!");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Khung giờ tập luyện (startTime và endTime) không được để trống!");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Giờ bắt đầu (startTime) phải trước giờ kết thúc (endTime)!");
        }

        // 1. KIỂM TRA KHÓA HUẤN LUYỆN
        TrainingLockStatusResponse lockStatus = injuryRecordService.getTrainingLockStatus(request.getHorseId());
        if (lockStatus.isLocked()) {
            throw new IllegalStateException("Chiến mã này đang bị KHÓA HUẤN LUYỆN (Trạng thái: "
                    + lockStatus.getCurrentStatus() + "). Không thể tạo kế hoạch mới!");
        }

        // 2. Lấy thông tin Course và danh sách bài tập liên kết
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
        if (courseSubjects.isEmpty()) {
            throw new IllegalStateException("Khóa học này chưa có bài tập nào, không thể gán cho ngựa!");
        }

        // 3. Chuẩn bị danh sách thứ tập (mặc định T2, T4, T6)
        Set<TrainingDay> selectedDays = (request.getTrainingDays() != null && !request.getTrainingDays().isEmpty())
                ? Set.copyOf(request.getTrainingDays())
                : Set.of(TrainingDay.MONDAY, TrainingDay.WEDNESDAY, TrainingDay.FRIDAY);

        // 4. TÍNH TOÁN TRƯỚC NGÀY KẾT THÚC DỰ KIẾN (calculatedEndDate)
        LocalDate calculatedEndDate = request.getStartDate();
        int simulatedCount = 0;
        int totalNeeded = course.getTotalSessions();

        while (simulatedCount < totalNeeded) {
            if (selectedDays.contains(TrainingDay.valueOf(calculatedEndDate.getDayOfWeek().name()))) {
                simulatedCount++;
            }
            if (simulatedCount < totalNeeded) {
                calculatedEndDate = calculatedEndDate.plusDays(1);
            }
        }

        // 5. KIỂM TRA XUNG ĐỘT KHOẢNG THỜI GIAN VỚI CÁC KẾ HOẠCH ACTIVE / UPCOMING
        List<HorseTrainingPlan> ongoingPlans = planRepository.findByHorseIdAndStatusInOrderByEndDateDesc(
                request.getHorseId(), List.of(TrainingPlanStatus.ACTIVE, TrainingPlanStatus.UPCOMING));

        for (HorseTrainingPlan existing : ongoingPlans) {
            // Hai khoảng [startDate, calculatedEndDate] và [existing.startDate, existing.endDate] giao nhau khi:
            boolean isOverlapping = !request.getStartDate().isAfter(existing.getEndDate())
                    && !calculatedEndDate.isBefore(existing.getStartDate());

            if (isOverlapping) {
                throw new IllegalStateException(String.format(
                        "Xung đột lịch: Chiến mã đã có kế hoạch huấn luyện (%s) từ ngày %s đến %s (trùng với thời gian dự kiến: %s đến %s)!",
                        existing.getStatus(),
                        existing.getStartDate(),
                        existing.getEndDate(),
                        request.getStartDate(),
                        calculatedEndDate));
            }
        }

        Long trainerId = currentUser.getUserId();

        // 6. Tạo HorseTrainingPlan
        HorseTrainingPlan plan = new HorseTrainingPlan();
        plan.setHorseId(request.getHorseId());
        plan.setCourseId(request.getCourseId());
        plan.setTrainerId(trainerId);
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(calculatedEndDate);
        plan.setStatus(request.getStartDate().isAfter(LocalDate.now()) ? TrainingPlanStatus.UPCOMING : TrainingPlanStatus.ACTIVE);
        plan.setNotes(request.getNotes());

        HorseTrainingPlan savedPlan = planRepository.save(plan);

        // 7. TỰ ĐỘNG SINH CÁC BUỔI TẬP (TRAINING WORKOUTS) VÀ KIỂM TRA XUNG ĐỘT LỊCH CỦA TRAINER
        List<TrainingWorkout> createdWorkouts = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();
        int workoutCount = 0;

        while (workoutCount < totalNeeded) {
            TrainingDay currentDay = TrainingDay.valueOf(currentDate.getDayOfWeek().name());
            if (selectedDays.contains(currentDay)) {
                LocalDateTime workoutStartTime = LocalDateTime.of(currentDate, request.getStartTime());
                LocalDateTime workoutEndTime = LocalDateTime.of(currentDate, request.getEndTime());

                // Kiểm tra xem Trainer đã có lịch huấn luyện chiến mã nào khác vào giờ này chưa
                List<TrainingWorkout> conflicts = workoutRepository.findConflictingWorkoutsForTrainer(
                        trainerId, workoutStartTime, workoutEndTime);
                if (!conflicts.isEmpty()) {
                    TrainingWorkout conflict = conflicts.getFirst();
                    String conflictingHorseName = horseRepository.findById(conflict.getHorseId())
                            .map(Horse::getName)
                            .orElse("Mã số #" + conflict.getHorseId());

                    throw new IllegalStateException(String.format(
                            "Trùng lịch: Bạn (Trainer) đã có lịch huấn luyện cho chiến mã '%s' vào ngày %s trong khung giờ %s - %s!",
                            conflictingHorseName,
                            currentDate,
                            conflict.getStartTime().toLocalTime(),
                            conflict.getEndTime().toLocalTime()));
                }

                CourseSubject currentCS = courseSubjects.get(workoutCount % courseSubjects.size());
                Subject currentSubject = subjectRepository.findById(currentCS.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found with id: " + currentCS.getSubjectId()));

                TrainingWorkout workout = new TrainingWorkout();
                workout.setPlanId(savedPlan.getId());
                workout.setSubjectId(currentSubject.getId());
                workout.setHorseId(request.getHorseId());
                workout.setAssignedToId(request.getGroomId()); // Gán Groom phụ trách
                workout.setWorkoutDate(currentDate);
                workout.setStartTime(workoutStartTime);
                workout.setEndTime(workoutEndTime);
                workout.setWorkoutType(currentSubject.getName().toUpperCase().contains("TRIAL RUN") ? WorkoutType.TRIAL_RUN : WorkoutType.REGULAR);
                workout.setStatus(WorkoutStatus.SCHEDULED);

                createdWorkouts.add(workoutRepository.save(workout));
                workoutCount++;
            }
            if (workoutCount < totalNeeded) {
                currentDate = currentDate.plusDays(1);
            }
        }

        savedPlan.setEndDate(currentDate);
        savedPlan = planRepository.save(savedPlan);

        return new HorseTrainingPlanDetailResponse(savedPlan, createdWorkouts);
    }

    @Transactional
    public HorseTrainingPlan updatePlanStatus(Long id, UpdatePlanStatusRequest request) {
        HorseTrainingPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training plan not found with id: " + id));

        plan.setStatus(request.getStatus());
        return planRepository.save(plan);
    }
}
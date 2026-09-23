package com.rtms.backend.service;

import com.rtms.backend.dto.CreateGroomDailyTaskRequest;
import com.rtms.backend.entity.GroomDailyTask;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.GroomTaskType;
import com.rtms.backend.repository.GroomDailyTaskRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.StableStallRepository;
import com.rtms.backend.repository.UserRepository;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroomDailyTaskService {

    private final GroomDailyTaskRepository taskRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;
    private final StableStallRepository stableStallRepository;

    public GroomDailyTaskService(GroomDailyTaskRepository taskRepository,
                                 HorseRepository horseRepository,
                                 UserRepository userRepository,
                                 StableStallRepository stableStallRepository) {
        this.taskRepository = taskRepository;
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;
        this.stableStallRepository = stableStallRepository;
    }

    @Transactional
    public GroomDailyTask createTask(CreateGroomDailyTaskRequest request, AuthenticatedUser currentUser) {
        if (request.getHorseId() == null) {
            throw new RuntimeException("Horse ID is required");
        }
        horseRepository.findById(request.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + request.getHorseId()));

        if (request.getTaskType() == null) {
            throw new RuntimeException("Task type is required");
        }

        Long targetGroomId;
        if ("GROOM".equalsIgnoreCase(currentUser.getRole())) {
            targetGroomId = currentUser.getUserId();
        } else {
            if (request.getGroomId() == null) {
                throw new RuntimeException("Groom ID is required when assigning a task");
            }
            userRepository.findById(request.getGroomId())
                    .orElseThrow(() -> new RuntimeException("Groom not found with id: " + request.getGroomId()));
            targetGroomId = request.getGroomId();
        }

        LocalDateTime scheduledTime = request.getScheduledTime() != null
                ? request.getScheduledTime()
                : LocalDateTime.now();

        GroomDailyTask task = new GroomDailyTask(
                targetGroomId,
                request.getHorseId(),
                request.getTaskType(),
                scheduledTime,
                request.getNotes()
        );

        return taskRepository.save(task);
    }

    public List<GroomDailyTask> getDailyTasks(Long groomId, LocalDate date, AuthenticatedUser currentUser) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(23, 59, 59, 999999999);

        if ("GROOM".equalsIgnoreCase(currentUser.getRole())) {
            return taskRepository.findByGroomIdAndScheduledTimeBetween(currentUser.getUserId(), start, end);
        }

        if (groomId != null) {
            return taskRepository.findByGroomIdAndScheduledTimeBetween(groomId, start, end);
        }

        return taskRepository.findByScheduledTimeBetween(start, end);
    }

    @Transactional
    public GroomDailyTask completeTask(Long taskId, AuthenticatedUser currentUser) {
        GroomDailyTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if ("GROOM".equalsIgnoreCase(currentUser.getRole()) && !task.getGroomId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You are not authorized to complete a task assigned to another groom");
        }

        task.setIsCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public List<GroomDailyTask> generateDailyRoutineTasks(LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        // Lấy tất cả ngựa đang ở trong chuồng
        List<Horse> horsesInStalls = horseRepository.findByCurrentStallIdIsNotNull();
        List<GroomDailyTask> generatedTasks = new ArrayList<>();

        for (Horse horse : horsesInStalls) {
            // Tìm chuồng của ngựa
            StableStall stall = stableStallRepository.findById(horse.getCurrentStallId()).orElse(null);
            if (stall == null || stall.getGroomId() == null) {
                // Nếu chuồng chưa được gán Groom phụ trách thì tạm thời bỏ qua
                continue;
            }

            Long groomId = stall.getGroomId();

            // Định nghĩa 5 task chuẩn theo SOP
            createTaskIfNotExist(generatedTasks, groomId, horse.getId(), GroomTaskType.FEEDING,
                    targetDate.atTime(5, 30), "Cho ăn sáng theo khẩu phần dinh dưỡng đã duyệt");

            createTaskIfNotExist(generatedTasks, groomId, horse.getId(), GroomTaskType.MUCKING_OUT,
                    targetDate.atTime(6, 0), "Dọn phân, thay rơm lót chuồng đợt 1 trước giờ tập");

            createTaskIfNotExist(generatedTasks, groomId, horse.getId(), GroomTaskType.GROOMING,
                    targetDate.atTime(9, 30), "Ca tắm rửa toàn thân, chải lông và bôi dầu dưỡng móng sau buổi tập (09:30 - 11:00)");

            createTaskIfNotExist(generatedTasks, groomId, horse.getId(), GroomTaskType.FEEDING,
                    targetDate.atTime(11, 30), "Cho ăn trưa và kiểm tra bổ sung máng nước sạch");

            createTaskIfNotExist(generatedTasks, groomId, horse.getId(), GroomTaskType.FEEDING,
                    targetDate.atTime(16, 30), "Cho ăn chiều, bổ sung cỏ khô và dọn vệ sinh chuồng đợt 2");
        }

        if (generatedTasks.isEmpty()) {
            return new ArrayList<>();
        }

        return taskRepository.saveAll(generatedTasks);
    }

    private void createTaskIfNotExist(List<GroomDailyTask> list, Long groomId, Long horseId,
                                      GroomTaskType taskType, LocalDateTime scheduledTime,
                                      String notes) {
        LocalDateTime checkStart = scheduledTime.minusMinutes(30);
        LocalDateTime checkEnd = scheduledTime.plusMinutes(30);
        boolean exists = taskRepository.existsByHorseIdAndTaskTypeAndScheduledTimeBetween(
                horseId, taskType, checkStart, checkEnd);
        if (!exists) {
            list.add(new GroomDailyTask(groomId, horseId, taskType, scheduledTime, notes));
        }
    }
}

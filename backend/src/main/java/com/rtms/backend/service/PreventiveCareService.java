package com.rtms.backend.service;

import com.rtms.backend.dto.CompletePreventiveCareScheduleRequest;
import com.rtms.backend.dto.CreatePreventiveCareScheduleRequest;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.HealthRecord;
import com.rtms.backend.entity.PreventiveCareSchedule;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.HealthRecordRepository;
import com.rtms.backend.repository.PreventiveCareScheduleRepository;
import com.rtms.backend.security.AuthenticatedUser;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreventiveCareService {

    private final PreventiveCareScheduleRepository scheduleRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final HorseRepository horseRepository;

    public PreventiveCareService(PreventiveCareScheduleRepository scheduleRepository,
            HealthRecordRepository healthRecordRepository,
            HorseRepository horseRepository) {
        this.scheduleRepository = scheduleRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.horseRepository = horseRepository;
    }

    public PreventiveCareSchedule createSchedule(CreatePreventiveCareScheduleRequest request) {
        PreventiveCareSchedule schedule = new PreventiveCareSchedule();
        schedule.setHorseId(request.getHorseId());
        schedule.setVeterinarianId(request.getVeterinarianId());
        schedule.setCareType(request.getCareType());
        schedule.setScheduledDate(request.getScheduledDate());
        schedule.setDescription(request.getDescription());
        // status mặc định "SCHEDULED" đã set sẵn trong Entity

        return scheduleRepository.save(schedule);
    }

    public List<PreventiveCareSchedule> getSchedulesByHorse(Long horseId, AuthenticatedUser currentUser)
            throws AccessDeniedException {
        if ("HORSE_OWNER".equals(currentUser.getRole())) {
            Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Horse not found with id: " + horseId));

            if (!currentUser.getUserId().equals(horse.getOwnerId())) {
                throw new AccessDeniedException("You can only view schedules for horses you own");
            }
        }
        return scheduleRepository.findByHorseIdOrderByScheduledDateAsc(horseId);
    }

    @Transactional
    public HealthRecord recordCompletion(Long scheduleId, CompletePreventiveCareScheduleRequest request, AuthenticatedUser currentUser) {
        PreventiveCareSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        HealthRecord record = new HealthRecord();
        record.setHorseId(schedule.getHorseId());
        record.setPreventiveCareScheduleId(schedule.getId());
        record.setRecordType(schedule.getCareType());
        
        // VeterinarianId from request if provided, otherwise from schedule, otherwise from current user if they are a vet
        if (request.getVeterinarianId() != null) {
            record.setVeterinarianId(request.getVeterinarianId());
        } else if (schedule.getVeterinarianId() != null) {
            record.setVeterinarianId(schedule.getVeterinarianId());
        } else {
            record.setVeterinarianId(currentUser.getUserId());
        }

        record.setExaminedAt(request.getPerformedAt() != null ? request.getPerformedAt() : LocalDateTime.now());
        record.setProductOrService(request.getProductOrService());
        record.setFindings(request.getResult());
        record.setNotes(request.getNotes());
        record.setFollowUpDate(request.getNextDueDate());

        HealthRecord savedRecord = healthRecordRepository.save(record);

        schedule.setStatus("COMPLETED");
        scheduleRepository.save(schedule);
        
        if (request.getNextDueDate() != null) {
            PreventiveCareSchedule nextSchedule = new PreventiveCareSchedule();
            nextSchedule.setHorseId(schedule.getHorseId());
            nextSchedule.setVeterinarianId(schedule.getVeterinarianId());
            nextSchedule.setCareType(schedule.getCareType());
            nextSchedule.setScheduledDate(request.getNextDueDate());
            nextSchedule.setDescription("Follow-up/Next due for: " + schedule.getCareType());
            nextSchedule.setStatus("PENDING");
            scheduleRepository.save(nextSchedule);
        }

        return savedRecord;
    }
}

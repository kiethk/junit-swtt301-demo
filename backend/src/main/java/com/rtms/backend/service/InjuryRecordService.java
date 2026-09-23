package com.rtms.backend.service;

import com.rtms.backend.dto.CreateInjuryRecordRequest;
import com.rtms.backend.dto.TrainingLockStatusResponse;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.HorseBodyRegion;
import com.rtms.backend.entity.InjuryRecord;
import com.rtms.backend.entity.HealthRecord;
import com.rtms.backend.enums.HorseStatus;
import com.rtms.backend.repository.HorseBodyRegionRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.InjuryRecordRepository;
import com.rtms.backend.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InjuryRecordService {

    private final InjuryRecordRepository injuryRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final HorseRepository horseRepository;
    private final HorseBodyRegionRepository horseBodyRegionRepository;

    public InjuryRecordService(InjuryRecordRepository injuryRecordRepository,
            HealthRecordRepository healthRecordRepository,
            HorseRepository horseRepository,
            HorseBodyRegionRepository horseBodyRegionRepository) {
        this.injuryRecordRepository = injuryRecordRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.horseRepository = horseRepository;
        this.horseBodyRegionRepository = horseBodyRegionRepository;
    }

    @Transactional
    public InjuryRecord createInjuryRecord(CreateInjuryRecordRequest request) {
        HealthRecord healthRecord = healthRecordRepository.findById(request.getHealthRecordId())
                .orElseThrow(() -> new RuntimeException("Health record not found with id: "
                        + request.getHealthRecordId()));

        Horse horse = horseRepository.findById(healthRecord.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found with id: "
                        + healthRecord.getHorseId()));

        HorseBodyRegion bodyRegion = horseBodyRegionRepository.findById(request.getBodyRegionId())
                .orElseThrow(() -> new RuntimeException("Body region not found with id: "
                        + request.getBodyRegionId()));

        if (!bodyRegion.isActive()) {
            throw new RuntimeException("Body region not found with id: " + request.getBodyRegionId());
        }

        InjuryRecord injury = new InjuryRecord();
        injury.setHorseId(healthRecord.getHorseId());
        injury.setHealthRecordId(healthRecord.getId());
        injury.setBodyRegionId(bodyRegion.getId());
        injury.setInjuryType(request.getInjuryType());
        injury.setSeverity(request.getSeverity());
        injury.setDescription(request.getDescription());
        injury.setDiagnosedAt(request.getDiagnosedAt());
        injury.setExpectedRecoveryDate(request.getExpectedRecoveryDate());
        injury.setPositionX(request.getPositionX());
        injury.setPositionY(request.getPositionY());
        injury.setPositionZ(request.getPositionZ());

        InjuryRecord savedInjury = injuryRecordRepository.save(injury);

        // (@Transactional đảm bảo lưu InjuryRecord và cập nhật Horse luôn thực hiện
        // cùng nhau)
        horse.setCurrentStatus(HorseStatus.INJURED);
        horseRepository.save(horse);

        return savedInjury;
    }

    public TrainingLockStatusResponse getTrainingLockStatus(Long horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + horseId));

        boolean locked = horse.getCurrentStatus() != HorseStatus.ELIGIBLE;
        return new TrainingLockStatusResponse(
                horseId,
                horse.getCurrentStatus().name(),
                locked);
    }
}

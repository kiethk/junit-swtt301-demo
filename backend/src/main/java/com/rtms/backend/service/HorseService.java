package com.rtms.backend.service;

import com.rtms.backend.dto.CreateHorseRequest;
import com.rtms.backend.dto.UpdateHorseStatusRequest;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.HorseStatus;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.StableStallRepository;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorseService {

    private final HorseRepository horseRepository;
    private final StableStallRepository stableStallRepository;

    public HorseService(HorseRepository horseRepository, StableStallRepository stableStallRepository) {
        this.horseRepository = horseRepository;
        this.stableStallRepository = stableStallRepository;
    }

    public List<Horse> getAllHorses(AuthenticatedUser currentUser) {
        if ("HORSE_OWNER".equals(currentUser.getRole())) {
            return horseRepository.findByOwnerId(currentUser.getUserId());
        }
        return horseRepository.findAll();
    }

    public Horse createHorse(CreateHorseRequest request) {
        Horse horse = new Horse();
        horse.setName(request.getName());
        horse.setBreed(request.getBreed());
        horse.setDateOfBirth(request.getDateOfBirth());
        horse.setStableLocation(request.getStableLocation());
        horse.setOwnerId(request.getOwnerId());
        // currentStatus KHONG set o day - de mac dinh "ELIGIBLE" theo gia tri default
        // trong Entity/DB
        return horseRepository.save(horse);
    }

    public Horse getHorseById(Long id, AuthenticatedUser currentUser) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + id));
        if ("HORSE_OWNER".equals(currentUser.getRole())) {
            if (!currentUser.getUserId().equals(horse.getOwnerId())) {
                throw new AccessDeniedException("You can only view horses you own");
            }
        }
        return horse;
    }

    public Horse updateHorseStatus(Long id, UpdateHorseStatusRequest request) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + id));

        // Cập nhật trạng thái mới
        horse.setCurrentStatus(HorseStatus.valueOf(request.getStatus()));

        return horseRepository.save(horse);
    }

    @Transactional
    public Horse assignStall(Long horseId, Long stallId) {
        if (horseId == null) {
            throw new RuntimeException("Horse ID is required");
        }
        if (stallId == null) {
            throw new RuntimeException("Stall ID is required");
        }

        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + horseId));

        StableStall newStall = stableStallRepository.findById(stallId)
                .orElseThrow(() -> new RuntimeException("Stable stall not found with id: " + stallId));

        // Nếu ngựa trước đó đã ở chuồng khác thì giải phóng chuồng cũ về AVAILABLE
        if (horse.getCurrentStallId() != null && !horse.getCurrentStallId().equals(stallId)) {
            stableStallRepository.findById(horse.getCurrentStallId()).ifPresent(oldStall -> {
                oldStall.setStatus(StallStatus.AVAILABLE);
                stableStallRepository.save(oldStall);
            });
        }

        newStall.setStatus(StallStatus.OCCUPIED);
        stableStallRepository.save(newStall);

        horse.setCurrentStallId(stallId);
        return horseRepository.save(horse);
    }

}
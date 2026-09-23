package com.rtms.backend.controller;

import com.rtms.backend.dto.ApiResponse;
import com.rtms.backend.dto.CreateRaceRegistrationRequest;
import com.rtms.backend.dto.ReviewRaceRegistrationRequest;
import com.rtms.backend.entity.RaceRegistration;
import com.rtms.backend.entity.User;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.RaceRegistrationRepository;
import com.rtms.backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/race-registrations")
public class RaceRegistrationController {

    private final RaceRegistrationRepository raceRegistrationRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    public RaceRegistrationController(
            RaceRegistrationRepository raceRegistrationRepository,
            HorseRepository horseRepository,
            UserRepository userRepository) {
        this.raceRegistrationRepository = raceRegistrationRepository;
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('RACE_REGISTRATION_VIEW')")
    @GetMapping
    public ApiResponse<List<RaceRegistration>> getAll(@RequestParam(required = false) Long horseId) {
        if (horseId != null) {
            return ApiResponse.success(raceRegistrationRepository.findByHorseId(horseId));
        }
        return ApiResponse.success(raceRegistrationRepository.findAll());
    }

    @PreAuthorize("hasAuthority('RACE_REGISTRATION_CREATE')")
    @PostMapping
    public ApiResponse<RaceRegistration> create(
            @RequestBody CreateRaceRegistrationRequest request,
            Authentication authentication) {
        horseRepository.findById(request.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found with id: " + request.getHorseId()));

        User trainer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RaceRegistration registration = new RaceRegistration();
        registration.setHorseId(request.getHorseId());
        registration.setTrainerId(trainer.getId());
        registration.setRaceName(request.getRaceName());
        registration.setLocation(request.getLocation());
        registration.setEventDate(request.getEventDate());
        registration.setDistanceMeters(request.getDistanceMeters());
        registration.setTrackType(request.getTrackType());
        registration.setJockeyName(request.getJockeyName());
        registration.setEntryFee(request.getEntryFee());
        registration.setTrainerNotes(request.getTrainerNotes());
        registration.setStatus("PENDING");

        return ApiResponse.success(raceRegistrationRepository.save(registration));
    }

    @PreAuthorize("hasAuthority('RACE_REGISTRATION_REVIEW')")
    @PatchMapping("/{id}/review")
    public ApiResponse<RaceRegistration> review(
            @PathVariable Long id,
            @RequestBody ReviewRaceRegistrationRequest request,
            Authentication authentication) {
        RaceRegistration registration = raceRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race registration not found with id: " + id));

        if (!"PENDING".equals(registration.getStatus())) {
            throw new RuntimeException("Race registration has already been reviewed");
        }

        String status = request.getStatus() == null ? "" : request.getStatus().toUpperCase();
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new RuntimeException("Status must be APPROVED or REJECTED");
        }

        User manager = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        registration.setStatus(status);
        registration.setManagerFeedback(request.getManagerFeedback());
        registration.setReviewedById(manager.getId());
        registration.setReviewedAt(LocalDateTime.now());

        return ApiResponse.success(raceRegistrationRepository.save(registration));
    }
}

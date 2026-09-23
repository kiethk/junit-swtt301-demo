package com.rtms.backend.repository;

import com.rtms.backend.entity.CandidateHorseProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateHorseProfileRepository
        extends JpaRepository<CandidateHorseProfile, Long> {

    Optional<CandidateHorseProfile> findByAdmissionId(Long admissionId);
}
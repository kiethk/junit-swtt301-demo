package com.rtms.backend.repository;

import com.rtms.backend.entity.RaceRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRegistrationRepository extends JpaRepository<RaceRegistration, Long> {
    List<RaceRegistration> findByHorseId(Long horseId);
}
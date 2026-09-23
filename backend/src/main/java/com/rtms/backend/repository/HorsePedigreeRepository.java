package com.rtms.backend.repository;

import com.rtms.backend.entity.HorsePedigree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HorsePedigreeRepository extends JpaRepository<HorsePedigree, Long> {
    Optional<HorsePedigree> findByHorseId(Long horseId);
}

package com.rtms.backend.repository;

import com.rtms.backend.entity.HorseBodyRegion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorseBodyRegionRepository extends JpaRepository<HorseBodyRegion, Long> {
    List<HorseBodyRegion> findByIsActiveTrueOrderByIdAsc();

}

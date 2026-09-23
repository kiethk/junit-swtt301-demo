package com.rtms.backend.repository;

import com.rtms.backend.entity.Area;
import com.rtms.backend.enums.AreaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByCode(String code);

    List<Area> findByType(AreaType type);
}
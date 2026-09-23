package com.rtms.backend.repository;

import com.rtms.backend.entity.InjuryRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InjuryRecordRepository extends JpaRepository<InjuryRecord, Long> {
    List<InjuryRecord> findByHorseIdOrderByDiagnosedAtDesc(Long horseId);

}

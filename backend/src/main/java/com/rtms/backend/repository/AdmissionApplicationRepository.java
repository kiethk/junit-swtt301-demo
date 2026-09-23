package com.rtms.backend.repository;

import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.enums.AdmissionStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionApplicationRepository
        extends JpaRepository<AdmissionApplication, Long> {

    List<AdmissionApplication> findByOwnerId(Long ownerId);

    List<AdmissionApplication> findByStatus(AdmissionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a
            FROM AdmissionApplication a
            WHERE a.id = :id
            """)
    Optional<AdmissionApplication> findByIdForUpdate(@Param("id") Long id);
}
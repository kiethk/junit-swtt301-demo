package com.rtms.backend.repository;

import com.rtms.backend.entity.AdmissionDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionDocumentRepository
        extends JpaRepository<AdmissionDocument, Long> {

    List<AdmissionDocument> findByAdmissionId(Long admissionId);
}
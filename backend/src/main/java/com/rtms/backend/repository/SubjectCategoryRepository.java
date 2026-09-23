package com.rtms.backend.repository;

import com.rtms.backend.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Long> {
}
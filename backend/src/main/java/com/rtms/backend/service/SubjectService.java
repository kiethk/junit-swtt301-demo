package com.rtms.backend.service;

import com.rtms.backend.dto.CreateSubjectRequest;
import com.rtms.backend.entity.Subject;
import com.rtms.backend.repository.SubjectCategoryRepository;
import com.rtms.backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectCategoryRepository categoryRepository;

    public SubjectService(SubjectRepository subjectRepository, SubjectCategoryRepository categoryRepository) {
        this.subjectRepository = subjectRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Subject> getAllSubjects(Long categoryId) {
        if (categoryId != null) {
            return subjectRepository.findByCategoryId(categoryId);
        }
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    @Transactional
    public Subject createSubject(CreateSubjectRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new IllegalArgumentException("Category not found with id: " + request.getCategoryId());
        }

        Subject subject = new Subject();
        subject.setCategoryId(request.getCategoryId());
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());
        subject.setSurfaceType(request.getSurfaceType());
        subject.setTargetDistanceMeters(request.getTargetDistanceMeters());
        subject.setIntensityLevel(request.getIntensityLevel());

        return subjectRepository.save(subject);
    }
}
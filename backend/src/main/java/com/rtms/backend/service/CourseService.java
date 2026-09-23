package com.rtms.backend.service;

import com.rtms.backend.dto.CourseDetailResponse;
import com.rtms.backend.dto.CourseSubjectItemRequest;
import com.rtms.backend.dto.CourseSubjectResponse;
import com.rtms.backend.dto.CreateCourseRequest;
import com.rtms.backend.entity.Course;
import com.rtms.backend.enums.CourseStatus;
import com.rtms.backend.entity.CourseSubject;
import com.rtms.backend.entity.Subject;
import com.rtms.backend.repository.CourseRepository;
import com.rtms.backend.repository.CourseSubjectRepository;
import com.rtms.backend.repository.SubjectRepository;
import com.rtms.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final SubjectRepository subjectRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseSubjectRepository courseSubjectRepository,
                         SubjectRepository subjectRepository) {
        this.courseRepository = courseRepository;
        this.courseSubjectRepository = courseSubjectRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public CourseDetailResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseIdOrderByOrderIndexAsc(id);
        List<CourseSubjectResponse> responseSubjects = mapToResponseSubjects(courseSubjects);

        return new CourseDetailResponse(course, responseSubjects);
    }

    @Transactional
    public CourseDetailResponse createCourse(CreateCourseRequest request, AuthenticatedUser currentUser) {
        if (courseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Khóa học với tên '" + request.getName() + "' đã tồn tại!");
        }

        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTargetGoal(request.getTargetGoal());
        course.setTotalSessions(request.getTotalSessions());
        course.setCreatedById(currentUser.getUserId());
        course.setStatus(CourseStatus.ACTIVE);

        Course savedCourse = courseRepository.save(course);

        List<CourseSubject> savedCourseSubjects = new ArrayList<>();
        if (request.getSubjects() != null && !request.getSubjects().isEmpty()) {
            int defaultIndex = 1;
            for (CourseSubjectItemRequest itemReq : request.getSubjects()) {
                // Kiểm tra bài tập có tồn tại trong ngân hàng không
                if (!subjectRepository.existsById(itemReq.getSubjectId())) {
                    throw new IllegalArgumentException("Subject not found with id: " + itemReq.getSubjectId());
                }

                CourseSubject cs = new CourseSubject();
                cs.setCourseId(savedCourse.getId());
                cs.setSubjectId(itemReq.getSubjectId());
                cs.setOrderIndex(itemReq.getOrderIndex() != null ? itemReq.getOrderIndex() : defaultIndex++);

                savedCourseSubjects.add(courseSubjectRepository.save(cs));
            }
        }

        return new CourseDetailResponse(savedCourse, mapToResponseSubjects(savedCourseSubjects));
    }

    @Transactional
    public CourseSubjectResponse addSubjectToCourse(Long courseId, CourseSubjectItemRequest request) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + request.getSubjectId()));

        CourseSubject cs = new CourseSubject();
        cs.setCourseId(courseId);
        cs.setSubjectId(request.getSubjectId());
        cs.setOrderIndex(request.getOrderIndex());

        CourseSubject saved = courseSubjectRepository.save(cs);
        return new CourseSubjectResponse(saved.getId(), subject.getId(), subject.getName(),
                subject.getDescription(), subject.getSurfaceType(), subject.getTargetDistanceMeters(),
                subject.getIntensityLevel(), saved.getOrderIndex());
    }

    private List<CourseSubjectResponse> mapToResponseSubjects(List<CourseSubject> courseSubjects) {
        List<CourseSubjectResponse> list = new ArrayList<>();
        for (CourseSubject cs : courseSubjects) {
            subjectRepository.findById(cs.getSubjectId()).ifPresent(sub -> {
                list.add(new CourseSubjectResponse(
                        cs.getId(),
                        sub.getId(),
                        sub.getName(),
                        sub.getDescription(),
                        sub.getSurfaceType(),
                        sub.getTargetDistanceMeters(),
                        sub.getIntensityLevel(),
                        cs.getOrderIndex()
                ));
            });
        }
        return list;
    }
}
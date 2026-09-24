package com.rtms.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.CandidateHorseProfileRepository;
import com.rtms.backend.repository.StableStallRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.HorsePedigreeRepository;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.HorsePedigree;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.ReviewDecision;
import com.rtms.backend.dto.ManagerReviewRequest;
import com.rtms.backend.entity.CandidateHorseProfile;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.HorseStatus;
import com.rtms.backend.enums.StallStatus;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AdmissionManagerReviewServiceTest {

        @Mock
        private AdmissionApplicationRepository admissionApplicationRepository;

        @Mock
        private CandidateHorseProfileRepository candidateHorseProfileRepository;

        @Mock
        private StableStallRepository stableStallRepository;

        @Mock
        private HorseRepository horseRepository;

        @Mock
        private HorsePedigreeRepository horsePedigreeRepository;

        @InjectMocks
        private AdmissionManagerReviewService service;

        @Test
        void should_ThrowException_When_AdmissionNotFound() {
                // Arrange
                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.empty());

                // Act
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> service.review(1L, 100L, null));
                // Assert
                assertEquals("Admission not found", exception.getMessage());
        }

        @Test
        void should_ThrowException_When_StatusIsNotManagerReview() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setStatus(AdmissionStatus.APPROVED);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                // Act & Assert
                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> service.review(1L, 100L, null));

                assertEquals(
                                "Admission is not ready for manager review",
                                exception.getMessage());
        }

        @Test
        void should_ThrowException_When_DecisionIsNull() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(null);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> service.review(1L, 100L, request));

                assertEquals(
                                "Decision is required",
                                exception.getMessage());
        }

        @Test
        void should_RejectAdmissionAndNotCreateHorse_When_ManagerRejects() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);
                admission.setQuarantineStallId(null);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.REJECTED);
                request.setFeedback("Horse does not meet admission requirements.");

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                when(admissionApplicationRepository.save(admission))
                                .thenReturn(admission);

                // Act
                AdmissionApplication result = service.review(1L, 100L, request);

                // Assert
                assertEquals(AdmissionStatus.REJECTED, result.getStatus());

                verify(admissionApplicationRepository).save(admission);

                verify(horseRepository, never())
                                .save(any(Horse.class));

                verify(horsePedigreeRepository, never())
                                .save(any(HorsePedigree.class));
        }

        @Test
        void should_ThrowException_When_RejectWithoutFeedback() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.REJECTED);
                request.setFeedback("");

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> service.review(1L, 100L, request));

                assertEquals(
                                "Feedback is required when rejecting an admission",
                                exception.getMessage());

                verify(admissionApplicationRepository, never())
                                .save(any(AdmissionApplication.class));

                verify(horseRepository, never())
                                .save(any(Horse.class));
        }

        @Test
        void should_ThrowException_When_NoRegularStall() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.APPROVED);
                request.setStallId(null);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                when(stableStallRepository.findFirstAvailableRegularStallForUpdate())
                                .thenReturn(Optional.empty());

                // Act & Assert
                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> service.review(1L, 100L, request));

                assertEquals(
                                "No available regular stall",
                                exception.getMessage());

                verify(horseRepository, never())
                                .save(any(Horse.class));

                verify(horsePedigreeRepository, never())
                                .save(any(HorsePedigree.class));

                verify(admissionApplicationRepository, never())
                                .save(any(AdmissionApplication.class));
        }

        @Test
        void should_CreateHorseAndCompleteAdmission_When_ManagerApproves() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setId(1L);
                admission.setOwnerId(10L);
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);
                admission.setQuarantineStallId(null);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.APPROVED);
                request.setFeedback("Approved by manager.");
                request.setStallId(null);

                StableStall regularStall = new StableStall();
                regularStall.setId(20L);
                regularStall.setStatus(StallStatus.AVAILABLE);

                CandidateHorseProfile candidate = new CandidateHorseProfile();
                candidate.setName("Thunder");
                candidate.setBreed("Thoroughbred");
                candidate.setRegistryName("RTMS Registry");
                candidate.setRegistrationNumber("REG-001");

                Horse savedHorse = new Horse();
                savedHorse.setId(30L);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                when(stableStallRepository.findFirstAvailableRegularStallForUpdate())
                                .thenReturn(Optional.of(regularStall));

                when(candidateHorseProfileRepository.findByAdmissionId(1L))
                                .thenReturn(Optional.of(candidate));

                when(horseRepository.save(any(Horse.class)))
                                .thenReturn(savedHorse);

                when(admissionApplicationRepository.save(admission))
                                .thenReturn(admission);

                // Act
                AdmissionApplication result = service.review(1L, 100L, request);

                // Assert
                assertEquals(AdmissionStatus.APPROVED, result.getStatus());
                assertEquals(ReviewDecision.APPROVED, result.getManagerDecision());
                assertEquals(100L, result.getManagerId());
                assertEquals(30L, result.getHorseId());
                assertEquals("Approved by manager.", result.getManagerFeedback());

                assertEquals(StallStatus.OCCUPIED, regularStall.getStatus());

                // Verify
                verify(horseRepository).save(any(Horse.class));
                verify(horsePedigreeRepository).save(any(HorsePedigree.class));
                verify(stableStallRepository).save(regularStall);
                verify(admissionApplicationRepository).save(admission);
        }

        @Test
        void should_CreateHorseWithCorrectData_When_ManagerApproves() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setId(1L);
                admission.setOwnerId(10L);
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);
                admission.setQuarantineStallId(null);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.APPROVED);
                request.setStallId(null);

                StableStall regularStall = new StableStall();
                regularStall.setId(20L);
                regularStall.setStatus(StallStatus.AVAILABLE);

                CandidateHorseProfile candidate = new CandidateHorseProfile();
                candidate.setName("Thunder");
                candidate.setBreed("Thoroughbred");
                candidate.setRegistryName("RTMS Registry");
                candidate.setRegistrationNumber("REG-001");

                Horse savedHorse = new Horse();
                savedHorse.setId(30L);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                when(stableStallRepository.findFirstAvailableRegularStallForUpdate())
                                .thenReturn(Optional.of(regularStall));

                when(candidateHorseProfileRepository.findByAdmissionId(1L))
                                .thenReturn(Optional.of(candidate));

                when(horseRepository.save(any(Horse.class)))
                                .thenReturn(savedHorse);

                when(admissionApplicationRepository.save(admission))
                                .thenReturn(admission);

                // Act
                service.review(1L, 100L, request);

                // Assert
                ArgumentCaptor<Horse> horseCaptor = ArgumentCaptor.forClass(Horse.class);

                verify(horseRepository).save(horseCaptor.capture());

                Horse createdHorse = horseCaptor.getValue();

                assertEquals("Thunder", createdHorse.getName());
                assertEquals("Thoroughbred", createdHorse.getBreed());
                assertEquals(10L, createdHorse.getOwnerId());
                assertEquals(HorseStatus.ELIGIBLE, createdHorse.getCurrentStatus());
                assertEquals(20L, createdHorse.getCurrentStallId());
                assertEquals("RTMS Registry", createdHorse.getRegistryName());
                assertEquals("REG-001", createdHorse.getRegistrationNumber());
        }

        @Test
        void should_ReleaseQuarantineStall_When_ManagerApproves() {

                // Arrange
                AdmissionApplication admission = new AdmissionApplication();
                admission.setId(1L);
                admission.setOwnerId(10L);
                admission.setStatus(AdmissionStatus.MANAGER_REVIEW);
                admission.setQuarantineStallId(5L);

                ManagerReviewRequest request = new ManagerReviewRequest();
                request.setDecision(ReviewDecision.APPROVED);
                request.setStallId(null);

                StableStall regularStall = new StableStall();
                regularStall.setId(20L);
                regularStall.setStatus(StallStatus.AVAILABLE);

                StableStall quarantineStall = new StableStall();
                quarantineStall.setId(5L);
                quarantineStall.setStatus(StallStatus.OCCUPIED);

                CandidateHorseProfile candidate = new CandidateHorseProfile();
                candidate.setName("Thunder");
                candidate.setBreed("Thoroughbred");

                Horse savedHorse = new Horse();
                savedHorse.setId(30L);

                when(admissionApplicationRepository.findByIdForUpdate(1L))
                                .thenReturn(Optional.of(admission));

                when(stableStallRepository.findFirstAvailableRegularStallForUpdate())
                                .thenReturn(Optional.of(regularStall));

                when(candidateHorseProfileRepository.findByAdmissionId(1L))
                                .thenReturn(Optional.of(candidate));

                when(horseRepository.save(any(Horse.class)))
                                .thenReturn(savedHorse);

                when(stableStallRepository.findById(5L))
                                .thenReturn(Optional.of(quarantineStall));

                when(admissionApplicationRepository.save(admission))
                                .thenReturn(admission);

                // Act
                AdmissionApplication result = service.review(1L, 100L, request);

                // Assert
                assertEquals(AdmissionStatus.APPROVED, result.getStatus());

                assertEquals(
                                StallStatus.OCCUPIED,
                                regularStall.getStatus());

                assertEquals(
                                StallStatus.AVAILABLE,
                                quarantineStall.getStatus());

                verify(stableStallRepository).save(regularStall);
                verify(stableStallRepository).save(quarantineStall);
        }
}
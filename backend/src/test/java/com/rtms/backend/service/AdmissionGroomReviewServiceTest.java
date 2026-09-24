package com.rtms.backend.service;

import com.rtms.backend.dto.GroomReviewRequest;
import com.rtms.backend.entity.AdmissionApplication;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.AdmissionStatus;
import com.rtms.backend.enums.ReviewDecision;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.AdmissionApplicationRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdmissionGroomReviewServiceTest {

    @Mock
    private AdmissionApplicationRepository admissionApplicationRepository;

    @Mock
    private StableStallRepository stableStallRepository;

    private AdmissionGroomReviewService service;

    @BeforeEach
    void setUp() {
        service = new AdmissionGroomReviewService(
                admissionApplicationRepository,
                stableStallRepository);
    }

    @Test
    @DisplayName("Groom approve with quarantine and downstream regular capacity moves admission to VET_REVIEW")
    void should_AllocateQuarantineStallAndMoveToVetReview_When_CapacityIsEnough() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        StableStall quarantineStall = stall(11L);
        GroomReviewRequest request = request(ReviewDecision.APPROVED, "Stable is ready");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(1, 2, 3);
        when(stableStallRepository.findFirstAvailableQuarantineStallForUpdate())
                .thenReturn(Optional.of(quarantineStall));
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.review(1L, 100L, request);

        // Assert
        assertEquals(AdmissionStatus.VET_REVIEW, result.getStatus());
        assertEquals(ReviewDecision.APPROVED, result.getGroomDecision());
        assertEquals(100L, result.getGroomId());
        assertEquals(11L, result.getQuarantineStallId());
        assertNotNull(result.getGroomReviewedAt());
        assertEquals(StallStatus.OCCUPIED, quarantineStall.getStatus());
        verify(stableStallRepository).save(quarantineStall);
        verify(admissionApplicationRepository).save(admission);
    }

    @Test
    @DisplayName("Groom approve without available quarantine stall moves admission to WAITING_FOR_STALL")
    void should_MoveToWaitingForStall_When_NoQuarantineCapacity() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.APPROVED, "Approved");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(0, 0, 5);
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.review(1L, 100L, request);

        // Assert
        assertEquals(AdmissionStatus.WAITING_FOR_STALL, result.getStatus());
        assertEquals(ReviewDecision.APPROVED, result.getGroomDecision());
        assertNull(result.getQuarantineStallId());
        verify(stableStallRepository, never()).findFirstAvailableQuarantineStallForUpdate();
        verify(stableStallRepository, never()).save(any(StableStall.class));
    }

    @Test
    @DisplayName("Groom approve without downstream regular capacity moves admission to WAITING_FOR_STALL")
    void should_MoveToWaitingForStall_When_RegularDownstreamCapacityIsNotEnough() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.APPROVED, "Approved");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(1, 3, 3);
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.review(1L, 100L, request);

        // Assert
        assertEquals(AdmissionStatus.WAITING_FOR_STALL, result.getStatus());
        assertEquals(ReviewDecision.APPROVED, result.getGroomDecision());
        assertNull(result.getQuarantineStallId());
        verify(stableStallRepository, never()).findFirstAvailableQuarantineStallForUpdate();
        verify(stableStallRepository, never()).save(any(StableStall.class));
    }

    @Test
    @DisplayName("Groom reject moves admission to REJECTED and does not allocate a stall")
    void should_RejectAdmissionAndNotAllocateStall_When_GroomRejects() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.REJECTED, "Unsafe quarantine handling");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.review(1L, 100L, request);

        // Assert
        assertEquals(AdmissionStatus.REJECTED, result.getStatus());
        assertEquals(ReviewDecision.REJECTED, result.getGroomDecision());
        assertEquals("Unsafe quarantine handling", result.getGroomFeedback());
        verify(stableStallRepository, never()).lockAdmissionCapacityStalls();
        verify(stableStallRepository, never()).save(any(StableStall.class));
    }

    @Test
    @DisplayName("Groom reject requires feedback")
    void should_ThrowException_When_RejectWithoutFeedback() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.REJECTED, " ");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.review(1L, 100L, request));

        assertEquals("Feedback is required when rejecting an admission", exception.getMessage());
        verify(admissionApplicationRepository, never()).save(any(AdmissionApplication.class));
        verify(stableStallRepository, never()).save(any(StableStall.class));
    }

    @Test
    @DisplayName("Retry allocation from WAITING_FOR_STALL allocates quarantine stall and moves to VET_REVIEW")
    void should_AllocateAndMoveToVetReview_When_CapacityReturns() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.WAITING_FOR_STALL);
        admission.setGroomDecision(ReviewDecision.APPROVED);
        StableStall quarantineStall = stall(12L);

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(1, 0, 1);
        when(stableStallRepository.findFirstAvailableQuarantineStallForUpdate())
                .thenReturn(Optional.of(quarantineStall));
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.allocateWaitingAdmission(1L);

        // Assert
        assertEquals(AdmissionStatus.VET_REVIEW, result.getStatus());
        assertEquals(12L, result.getQuarantineStallId());
        assertEquals(StallStatus.OCCUPIED, quarantineStall.getStatus());
        verify(stableStallRepository).save(quarantineStall);
    }

    @Test
    @DisplayName("Retry allocation from WAITING_FOR_STALL requires a previous Groom approval")
    void should_ThrowException_When_AdmissionWasNotApprovedByGroom() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.WAITING_FOR_STALL);
        admission.setGroomDecision(null);

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.allocateWaitingAdmission(1L));

        assertEquals("Admission must be approved by Groom before allocation", exception.getMessage());
        verify(stableStallRepository, never()).lockAdmissionCapacityStalls();
        verify(admissionApplicationRepository, never()).save(any(AdmissionApplication.class));
    }

    @Test
    @DisplayName("Retry allocation keeps WAITING_FOR_STALL and does not save when capacity is still unavailable")
    void should_KeepWaitingWithoutSaving_When_CapacityIsStillUnavailable() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.WAITING_FOR_STALL);
        admission.setGroomDecision(ReviewDecision.APPROVED);

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(0, 0, 5);

        // Act
        AdmissionApplication result = service.allocateWaitingAdmission(1L);

        // Assert
        assertEquals(AdmissionStatus.WAITING_FOR_STALL, result.getStatus());
        assertNull(result.getQuarantineStallId());
        verify(stableStallRepository, never()).findFirstAvailableQuarantineStallForUpdate();
        verify(admissionApplicationRepository, never()).save(any(AdmissionApplication.class));
    }

    @ParameterizedTest(name = "availableQ={0}, occupiedQ={1}, availableRegular={2} => {3}")
    @CsvSource({
            "1, 0, 1, VET_REVIEW",
            "1, 1, 1, WAITING_FOR_STALL",
            "1, 2, 3, VET_REVIEW",
            "0, 0, 10, WAITING_FOR_STALL"
    })
    @DisplayName("Capacity boundary cases follow the quarantine and downstream regular capacity rule")
    void should_FollowBusinessRule_When_TestingCapacityBoundaryCases(
            long availableQuarantine,
            long occupiedQuarantine,
            long availableRegular,
            AdmissionStatus expectedStatus) {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.GROOM_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.APPROVED, "Approved");
        StableStall quarantineStall = stall(13L);

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));
        mockCapacity(availableQuarantine, occupiedQuarantine, availableRegular);
        if (expectedStatus == AdmissionStatus.VET_REVIEW) {
            when(stableStallRepository.findFirstAvailableQuarantineStallForUpdate())
                    .thenReturn(Optional.of(quarantineStall));
        }
        when(admissionApplicationRepository.save(admission))
                .thenReturn(admission);

        // Act
        AdmissionApplication result = service.review(1L, 100L, request);

        // Assert
        assertEquals(expectedStatus, result.getStatus());
    }

    @Test
    @DisplayName("Groom review is allowed only when admission is in GROOM_REVIEW")
    void should_ThrowException_When_AdmissionIsNotInGroomReview() {
        // Arrange
        AdmissionApplication admission = admission(1L, AdmissionStatus.VET_REVIEW);
        GroomReviewRequest request = request(ReviewDecision.APPROVED, "Approved");

        when(admissionApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(admission));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.review(1L, 100L, request));

        assertEquals("Admission is not ready for groom review", exception.getMessage());
        verify(admissionApplicationRepository, never()).save(any(AdmissionApplication.class));
    }

    private void mockCapacity(
            long availableQuarantine,
            long occupiedQuarantine,
            long availableRegular) {
        when(stableStallRepository.lockAdmissionCapacityStalls())
                .thenReturn(List.of());
        when(stableStallRepository.countByAreaTypeAndStatus("QUARANTINE", "AVAILABLE"))
                .thenReturn(availableQuarantine);
        when(stableStallRepository.countByAreaTypeAndStatus("QUARANTINE", "OCCUPIED"))
                .thenReturn(occupiedQuarantine);
        when(stableStallRepository.countByAreaTypeAndStatus("REGULAR", "AVAILABLE"))
                .thenReturn(availableRegular);
    }

    private AdmissionApplication admission(Long id, AdmissionStatus status) {
        AdmissionApplication admission = new AdmissionApplication();
        admission.setId(id);
        admission.setOwnerId(10L);
        admission.setStatus(status);
        return admission;
    }

    private StableStall stall(Long id) {
        StableStall stall = new StableStall();
        stall.setId(id);
        stall.setStatus(StallStatus.AVAILABLE);
        return stall;
    }

    private GroomReviewRequest request(ReviewDecision decision, String feedback) {
        GroomReviewRequest request = new GroomReviewRequest();
        request.setDecision(decision);
        request.setFeedback(feedback);
        return request;
    }
}

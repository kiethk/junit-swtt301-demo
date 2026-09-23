package com.rtms.backend.service;

import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.StallStatus;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.StableStallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorseServiceTest {

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private StableStallRepository stableStallRepository;

    private HorseService horseService;

    @BeforeEach
    void setUp() {
        horseService = new HorseService(horseRepository, stableStallRepository);
    }

    @Test
    @DisplayName("Gán chuồng thành công: Chuồng mới chuyển sang OCCUPIED, ngựa nhận currentStallId")
    void testAssignStall_Success() {
        Horse horse = new Horse();
        horse.setId(1L);
        horse.setName("Lightning Bolt");
        horse.setCurrentStallId(null);

        StableStall stall = new StableStall();
        stall.setId(6L);
        stall.setStatus(StallStatus.AVAILABLE);

        when(horseRepository.findById(1L)).thenReturn(Optional.of(horse));
        when(stableStallRepository.findById(6L)).thenReturn(Optional.of(stall));
        when(horseRepository.save(any(Horse.class))).thenAnswer(i -> i.getArgument(0));

        Horse updated = horseService.assignStall(1L, 6L);

        assertNotNull(updated);
        assertEquals(6L, updated.getCurrentStallId());
        assertEquals(StallStatus.OCCUPIED, stall.getStatus());
        verify(stableStallRepository, times(1)).save(stall);
        verify(horseRepository, times(1)).save(horse);
    }

    @Test
    @DisplayName("Đổi chuồng: Chuồng cũ về AVAILABLE, chuồng mới thành OCCUPIED")
    void testAssignStall_ChangeStall() {
        Horse horse = new Horse();
        horse.setId(1L);
        horse.setCurrentStallId(5L); // Chuồng cũ

        StableStall oldStall = new StableStall();
        oldStall.setId(5L);
        oldStall.setStatus(StallStatus.OCCUPIED);

        StableStall newStall = new StableStall();
        newStall.setId(6L);
        newStall.setStatus(StallStatus.AVAILABLE);

        when(horseRepository.findById(1L)).thenReturn(Optional.of(horse));
        when(stableStallRepository.findById(6L)).thenReturn(Optional.of(newStall));
        when(stableStallRepository.findById(5L)).thenReturn(Optional.of(oldStall));
        when(horseRepository.save(any(Horse.class))).thenAnswer(i -> i.getArgument(0));

        Horse updated = horseService.assignStall(1L, 6L);

        assertEquals(6L, updated.getCurrentStallId());
        assertEquals(StallStatus.AVAILABLE, oldStall.getStatus());
        assertEquals(StallStatus.OCCUPIED, newStall.getStatus());
        verify(stableStallRepository, times(1)).save(oldStall);
        verify(stableStallRepository, times(1)).save(newStall);
    }

    @Test
    @DisplayName("Lỗi khi chuồng không tồn tại")
    void testAssignStall_StallNotFound() {
        Horse horse = new Horse();
        horse.setId(1L);

        when(horseRepository.findById(1L)).thenReturn(Optional.of(horse));
        when(stableStallRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> horseService.assignStall(1L, 999L));
        verify(horseRepository, never()).save(any());
    }
}
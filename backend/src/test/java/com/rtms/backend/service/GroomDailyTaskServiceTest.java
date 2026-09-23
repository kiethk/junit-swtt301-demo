package com.rtms.backend.service;

import com.rtms.backend.entity.GroomDailyTask;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.StableStall;
import com.rtms.backend.enums.GroomTaskType;
import com.rtms.backend.repository.GroomDailyTaskRepository;
import com.rtms.backend.repository.HorseRepository;
import com.rtms.backend.repository.StableStallRepository;
import com.rtms.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroomDailyTaskServiceTest {

    @Mock
    private GroomDailyTaskRepository taskRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StableStallRepository stableStallRepository;

    private GroomDailyTaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new GroomDailyTaskService(
                taskRepository,
                horseRepository,
                userRepository,
                stableStallRepository
        );
    }

    @Test
    @DisplayName("1. Sinh việc thành công cho ngựa trong chuồng - Đủ 5 mốc việc chuẩn SOP")
    void testGenerateDailyRoutineTasks_Success() {
        // Arrange
        LocalDate targetDate = LocalDate.of(2026, 9, 22);

        Horse horse = new Horse();
        horse.setId(1L);
        horse.setName("Lightning Bolt");
        horse.setCurrentStallId(10L);

        StableStall stall = new StableStall();
        stall.setId(10L);
        stall.setGroomId(4L);

        when(horseRepository.findByCurrentStallIdIsNotNull()).thenReturn(List.of(horse));
        when(stableStallRepository.findById(10L)).thenReturn(Optional.of(stall));
        when(taskRepository.existsByHorseIdAndTaskTypeAndScheduledTimeBetween(anyLong(), any(), any(), any()))
                .thenReturn(false);
        when(taskRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<GroomDailyTask> result = taskService.generateDailyRoutineTasks(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size(), "Phải sinh đúng 5 task chuẩn theo SOP");

        // Task 1: 05:30 FEEDING (Ăn sáng)
        GroomDailyTask t1 = result.get(0);
        assertEquals(4L, t1.getGroomId());
        assertEquals(1L, t1.getHorseId());
        assertEquals(GroomTaskType.FEEDING, t1.getTaskType());
        assertEquals(LocalDateTime.of(2026, 9, 22, 5, 30), t1.getScheduledTime());

        // Task 2: 06:00 MUCKING_OUT (Dọn chuồng ca sáng)
        GroomDailyTask t2 = result.get(1);
        assertEquals(GroomTaskType.MUCKING_OUT, t2.getTaskType());
        assertEquals(LocalDateTime.of(2026, 9, 22, 6, 0), t2.getScheduledTime());

        // Task 3: 09:30 GROOMING (Tắm rửa, dưỡng móng sau tập)
        GroomDailyTask t3 = result.get(2);
        assertEquals(GroomTaskType.GROOMING, t3.getTaskType());
        assertEquals(LocalDateTime.of(2026, 9, 22, 9, 30), t3.getScheduledTime());

        // Task 4: 11:30 FEEDING (Ăn trưa)
        GroomDailyTask t4 = result.get(3);
        assertEquals(GroomTaskType.FEEDING, t4.getTaskType());
        assertEquals(LocalDateTime.of(2026, 9, 22, 11, 30), t4.getScheduledTime());

        // Task 5: 16:30 FEEDING (Ăn chiều)
        GroomDailyTask t5 = result.get(4);
        assertEquals(GroomTaskType.FEEDING, t5.getTaskType());
        assertEquals(LocalDateTime.of(2026, 9, 22, 16, 30), t5.getScheduledTime());

        verify(taskRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("2. Tính Idempotent - Nếu các task đã tồn tại thì không sinh trùng lặp")
    void testGenerateDailyRoutineTasks_Idempotent() {
        // Arrange
        LocalDate targetDate = LocalDate.of(2026, 9, 22);

        Horse horse = new Horse();
        horse.setId(1L);
        horse.setCurrentStallId(10L);

        StableStall stall = new StableStall();
        stall.setId(10L);
        stall.setGroomId(4L);

        when(horseRepository.findByCurrentStallIdIsNotNull()).thenReturn(List.of(horse));
        when(stableStallRepository.findById(10L)).thenReturn(Optional.of(stall));
        // Giả lập rằng task đã tồn tại trong DB
        when(taskRepository.existsByHorseIdAndTaskTypeAndScheduledTimeBetween(anyLong(), any(), any(), any()))
                .thenReturn(true);

        // Act
        List<GroomDailyTask> result = taskService.generateDailyRoutineTasks(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size(), "Không được sinh trùng lặp nếu task đã tồn tại");
        verify(taskRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("3. Không có ngựa nào trong chuồng - Trả về mảng rỗng")
    void testGenerateDailyRoutineTasks_NoHorsesInStalls() {
        // Arrange
        when(horseRepository.findByCurrentStallIdIsNotNull()).thenReturn(Collections.emptyList());

        // Act
        List<GroomDailyTask> result = taskService.generateDailyRoutineTasks(LocalDate.now());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("4. Ngựa có chuồng nhưng chuồng chưa gán Groom - Bỏ qua")
    void testGenerateDailyRoutineTasks_StallWithoutGroom() {
        // Arrange
        Horse horse = new Horse();
        horse.setId(1L);
        horse.setCurrentStallId(10L);

        StableStall stall = new StableStall();
        stall.setId(10L);
        stall.setGroomId(null); // Chưa gán Groom

        when(horseRepository.findByCurrentStallIdIsNotNull()).thenReturn(List.of(horse));
        when(stableStallRepository.findById(10L)).thenReturn(Optional.of(stall));

        // Act
        List<GroomDailyTask> result = taskService.generateDailyRoutineTasks(LocalDate.now());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, never()).saveAll(anyList());
    }
}
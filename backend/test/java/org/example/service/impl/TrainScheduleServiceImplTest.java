// TrainScheduleServiceImplTest.java
package org.example.service.impl;

import org.example.BaseSpringBootTest;
import org.example.mapper.TrainScheduleMapper;
import org.example.pojo.TrainSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainScheduleServiceImplTest extends BaseSpringBootTest {

    @Mock
    private TrainScheduleMapper trainScheduleMapper;

    @InjectMocks
    private TrainScheduleServiceImpl trainScheduleService;

    private TrainSchedule testSchedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testSchedule = new TrainSchedule();
        testSchedule.setId(1L);
        testSchedule.setTrainNumber("G123");
        testSchedule.setDate(LocalDate.now());
        testSchedule.setRouteId(1L);
        testSchedule.setDepartureTime(LocalDateTime.now());
        testSchedule.setArrivalTime(LocalDateTime.now().plusHours(5));
        testSchedule.setTicketTotal(100);
        testSchedule.setTicketSold(50);
        testSchedule.setPrice(new BigDecimal("300.00"));
    }

    @Test
    void getAllSchedules_Success() {
        // Given
        List<TrainSchedule> schedules = Arrays.asList(testSchedule, new TrainSchedule());
        when(trainScheduleMapper.findAll()).thenReturn(schedules);

        // When
        List<TrainSchedule> result = trainScheduleService.getAllSchedules();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainScheduleMapper).findAll();
    }

    @Test
    void addSchedule_Success() {
        // Given
        // 修复：正确使用 thenAnswer 来模拟 insert 方法的行为
        doAnswer(invocation -> {
            TrainSchedule schedule = invocation.getArgument(0);
            schedule.setId(1L);
            return 1;
        }).when(trainScheduleMapper).insert(any(TrainSchedule.class));

        // When
        TrainSchedule result = trainScheduleService.addSchedule(testSchedule);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(trainScheduleMapper).insert(any(TrainSchedule.class));
    }

    @Test
    void updateSchedule_Success() {
        // Given
        TrainSchedule updatedSchedule = new TrainSchedule();
        updatedSchedule.setId(1L);
        updatedSchedule.setTrainNumber("G456");
        updatedSchedule.setPrice(new BigDecimal("350.00"));

        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);
        doNothing().when(trainScheduleMapper).update(any(TrainSchedule.class)); // 修复：使用 doNothing()

        // When
        TrainSchedule result = trainScheduleService.updateSchedule(updatedSchedule);

        // Then
        assertNotNull(result);
        assertEquals("G456", result.getTrainNumber());
        assertEquals(new BigDecimal("350.00"), result.getPrice());
        verify(trainScheduleMapper).findById(1L);
        verify(trainScheduleMapper).update(any(TrainSchedule.class));
    }

    @Test
    void updateSchedule_NotFound_ThrowsException() {
        // Given
        when(trainScheduleMapper.findById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainScheduleService.updateSchedule(testSchedule);
        });

        assertEquals("车次不存在", exception.getMessage());
    }

    @Test
    void deleteSchedule_Success() {
        // Given
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);
        doNothing().when(trainScheduleMapper).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> {
            trainScheduleService.deleteSchedule(1L);
        });

        verify(trainScheduleMapper).findById(1L);
        verify(trainScheduleMapper).deleteById(1L);
    }

    @Test
    void deleteSchedule_NotFound_ThrowsException() {
        // Given
        when(trainScheduleMapper.findById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainScheduleService.deleteSchedule(999L);
        });

        assertEquals("车次不存在", exception.getMessage());
    }
}

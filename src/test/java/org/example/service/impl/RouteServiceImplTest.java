// RouteServiceImplTest.java
package org.example.service.impl;

import org.example.BaseSpringBootTest;
import org.example.mapper.RouteMapper;
import org.example.mapper.StationMapper;
import org.example.mapper.StopMapper;
import org.example.mapper.TrainScheduleMapper;
import org.example.pojo.Route;
import org.example.pojo.Station;
import org.example.pojo.Stop;
import org.example.pojo.TrainSchedule;
import org.example.pojo.TrainScheduleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteServiceImplTest extends BaseSpringBootTest {

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private StationMapper stationMapper;

    @Mock
    private StopMapper stopMapper;

    @Mock
    private TrainScheduleMapper trainScheduleMapper;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route testRoute;
    private Station fromStation;
    private Station toStation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setFromStationName("北京");
        testRoute.setToStationName("上海");

        fromStation = new Station();
        fromStation.setId(1L);
        fromStation.setStationName("北京站");

        toStation = new Station();
        toStation.setId(2L);
        toStation.setStationName("上海站");
    }

    @Test
    void searchRoutes_Success() {
        // Given
        Route route = new Route();
        route.setId(1L);
        route.setFromStationName("北京");
        route.setToStationName("上海");

        TrainSchedule schedule = new TrainSchedule();
        schedule.setId(1L);
        schedule.setTrainNumber("G1");
        schedule.setDate(LocalDate.now());
        schedule.setRouteId(1L);
        schedule.setDepartureTime(LocalDateTime.of(2023, 1, 1, 8, 0));
        schedule.setArrivalTime(LocalDateTime.of(2023, 1, 1, 13, 0));
        schedule.setPrice(new BigDecimal("500.00"));

        when(routeMapper.findAll()).thenReturn(Arrays.asList(route));
        when(trainScheduleMapper.findByRouteIdAndDate(1L, LocalDate.now(), 1, 5))
                .thenReturn(Arrays.asList(schedule));

        // When
        List<TrainScheduleVO> result = routeService.searchRoutes(null, null, 1, 5, LocalDate.now());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getTrainNo());
    }

    @Test
    void searchRoutes_EmptyResult() {
        // Given
        when(routeMapper.findAll()).thenReturn(Collections.emptyList());

        // When
        List<TrainScheduleVO> result = routeService.searchRoutes(null, null, 1, 5, LocalDate.now());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRoutes_Success() {
        // Given
        List<Route> routes = Arrays.asList(testRoute, new Route());
        when(routeMapper.findAll()).thenReturn(routes);

        // When
        List<Route> result = routeService.getAllRoutes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(routeMapper).findAll();
    }

    @Test
    void addRoute_Success() {
        // Given
        when(routeMapper.insert(any(Route.class))).thenAnswer(invocation -> {
            Route route = invocation.getArgument(0);
            route.setId(1L);
            return 1;
        });

        // When
        Route result = routeService.addRoute(testRoute);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        verify(routeMapper).insert(any(Route.class));
    }

    @Test
    void updateRoute_Success() {
        // Given
        Route updatedRoute = new Route();
        updatedRoute.setId(1L);
        updatedRoute.setFromStationName("天津");
        updatedRoute.setToStationName("广州");

        // 修复：添加 findById 的 mock 设置
        when(routeMapper.findById(1L)).thenReturn(testRoute);
        when(routeMapper.update(any(Route.class))).thenReturn(1);

        // When
        Route result = routeService.updateRoute(updatedRoute);

        // Then
        assertNotNull(result);
        assertEquals("天津", result.getFromStationName());
        assertEquals("广州", result.getToStationName());
        verify(routeMapper).findById(1L);  // 验证 findById 被调用
        verify(routeMapper).update(any(Route.class));
    }

    @Test
    void deleteRoute_Success() {
        // Given
        // 修复：添加 findById 的 mock 设置
        when(routeMapper.findById(1L)).thenReturn(testRoute);
        doNothing().when(routeMapper).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> {
            routeService.deleteRoute(1L);
        });

        verify(routeMapper).findById(1L);  // 验证 findById 被调用
        verify(routeMapper).deleteById(1L);
    }
}

// OrderServiceImplTest.java (补充完整)
package org.example.service.impl;

import org.example.BaseSpringBootTest;
import org.example.mapper.OrderMapper;
import org.example.mapper.RouteMapper;
import org.example.mapper.TrainScheduleMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest extends BaseSpringBootTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private TrainScheduleMapper trainScheduleMapper;

    @Mock
    private RouteMapper routeMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private TrainSchedule testSchedule;
    private Route testRoute;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setBalance(new BigDecimal("200.00"));

        testSchedule = new TrainSchedule();
        testSchedule.setId(1L);
        testSchedule.setPrice(new BigDecimal("50.00"));
        testSchedule.setTicketTotal(100);
        testSchedule.setTicketSold(50);

        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setFromStationName("北京");
        testRoute.setToStationName("上海");
    }

    @Test
    void createOrder_Success() {
        // Given
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);
        doAnswer(invocation -> {
            testSchedule.setTicketSold(51); // 模拟更新库存
            return null;
        }).when(orderMapper).insert(any());

        // When & Then
        assertDoesNotThrow(() -> {
            orderService.createOrder(1L, 1L, "张三", 1);
        });

        verify(userMapper).update(any());
        verify(orderMapper).insert(any());
    }

    @Test
    void createOrder_InsufficientBalance_ThrowsException() {
        // Given
        testUser.setBalance(new BigDecimal("10.00")); // 余额不足
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L, 1L, "张三", 1);
        });

        assertEquals("用户余额不足", exception.getMessage());
    }

    @Test
    void createOrder_InsufficientTickets_ThrowsException() {
        // Given
        testSchedule.setTicketSold(99); // 只剩1张票
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L, 1L, "张三", 2); // 要买2张票
        });

        assertEquals("库存不足", exception.getMessage());
    }


    @Test
    void getOrdersByUserId_EmptyList() {
        // Given
        when(orderMapper.selectOrdersByUserId(1L)).thenReturn(Collections.emptyList());

        // When
        var orders = orderService.getOrdersByUserId(1L);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void cancelOrder_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTrainScheduleId(1L);
        order.setPassengerName("张三");
        order.setTicketCount(2);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setStatus(1); // 已出票状态

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);
        when(userMapper.findById(1L)).thenReturn(testUser);

        // When & Then
        assertDoesNotThrow(() -> {
            orderService.cancelOrder(1L);
        });

        // Verify order was updated
        verify(orderMapper).update(any(Order.class));

        // Verify ticket count was restored
        assertEquals(48, testSchedule.getTicketSold()); // 50 - 2 = 48

        // Verify user balance was refunded
        assertEquals(new BigDecimal("300.00"), testUser.getBalance()); // 200 + 100 = 300
    }

    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(3); // 已取消状态

        when(orderMapper.selectById(1L)).thenReturn(order);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("订单已取消", exception.getMessage());
    }

    @Test
    void getOrderById_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTrainScheduleId(1L);
        order.setPassengerName("张三");
        order.setTicketCount(2);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setStatus(1);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(trainScheduleMapper.findById(1L)).thenReturn(testSchedule);
        when(routeMapper.findById(1L)).thenReturn(testRoute);

        // When
        OrderVO orderVO = orderService.getOrderById(1L);

        // Then
        assertNotNull(orderVO);
        assertEquals(1L, orderVO.getOrderId());
        assertEquals("张三", orderVO.getPassengerName());
        assertEquals(2, orderVO.getTicketCount());
        assertEquals(new BigDecimal("100.00"), orderVO.getTotalPrice());
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        // Given
        when(orderMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(999L);
        });

        assertEquals("订单不存在", exception.getMessage());
    }
}

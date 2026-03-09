// AdminControllerTest.java
package org.example.controller;

import org.example.BaseSpringBootTest;
import org.example.pojo.*;
import org.example.service.AdminService;
import org.example.service.OrderService;
import org.example.service.RouteService;
import org.example.service.TrainScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AdminControllerTest extends BaseSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private RouteService routeService;

    @MockBean
    private TrainScheduleService trainScheduleService;

    @MockBean
    private OrderService orderService;

    private String adminToken;

    @BeforeEach
    void authenticate() throws Exception {
        // 使用预设的有效token
        adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJhZG1pbk5hbWUiOiJhZG1pbiIsImFkbWluSWQiOjEsImV4cCI6MTc2NDc3NjYzM30.qpaYNJTpLqnfaLMKcEBw-NYGAhyzO4wGwXdY6cxKF0g";
    }

    @Test
    void adminLogin_Success() throws Exception {
        // Given
        AdminLoginInfo loginInfo = new AdminLoginInfo();
        loginInfo.setId(1L);
        loginInfo.setAdminName("admin");
        loginInfo.setToken("eyJhbGciOiJIUzI1NiJ9.eyJhZG1pbk5hbWUiOiJhZG1pbiIsImFkbWluSWQiOjEsImV4cCI6MTc2NDc3NjYzM30.qpaYNJTpLqnfaLMKcEBw-NYGAhyzO4wGwXdY6cxKF0g");

        when(adminService.login("admin", "admin123")).thenReturn(loginInfo);

        // When & Then
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"adminName\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data.adminName").value("admin"))
                .andExpect(jsonPath("$.data.token").value("eyJhbGciOiJIUzI1NiJ9.eyJhZG1pbk5hbWUiOiJhZG1pbiIsImFkbWluSWQiOjEsImV4cCI6MTc2NDc3NjYzM30.qpaYNJTpLqnfaLMKcEBw-NYGAhyzO4wGwXdY6cxKF0g"));
    }

    @Test
    void getAllSchedules_Success() throws Exception {
        // Given
        TrainSchedule schedule = new TrainSchedule();
        schedule.setId(1L);
        schedule.setTrainNumber("G123");
        schedule.setDate(LocalDate.now());
        schedule.setPrice(new BigDecimal("300.00"));

        when(trainScheduleService.getAllSchedules()).thenReturn(Arrays.asList(schedule));

        // When & Then
        mockMvc.perform(get("/admin/schedule")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data[0].trainNumber").value("G123"));
    }

    @Test
    void addSchedule_Success() throws Exception {
        // Given
        TrainSchedule schedule = new TrainSchedule();
        schedule.setId(1L);
        schedule.setTrainNumber("G123");
        schedule.setDate(LocalDate.now());
        schedule.setPrice(new BigDecimal("300.00"));

        when(trainScheduleService.addSchedule(any(TrainSchedule.class))).thenReturn(schedule);

        // When & Then
        mockMvc.perform(post("/admin/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainNumber\":\"G123\",\"date\":\"2023-01-01\",\"price\":300.00}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data.trainNumber").value("G123"));
    }

    @Test
    void updateSchedule_Success() throws Exception {
        // Given
        TrainSchedule schedule = new TrainSchedule();
        schedule.setId(1L);
        schedule.setTrainNumber("G456");
        schedule.setPrice(new BigDecimal("350.00"));

        when(trainScheduleService.updateSchedule(any(TrainSchedule.class))).thenReturn(schedule);

        // When & Then
        mockMvc.perform(put("/admin/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"trainNumber\":\"G456\",\"price\":350.00}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data.trainNumber").value("G456"));
    }

    @Test
    void deleteSchedule_Success() throws Exception {
        // Given
        doNothing().when(trainScheduleService).deleteSchedule(1L);

        // When & Then
        mockMvc.perform(delete("/admin/schedule/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS));
    }

    @Test
    void getAllOrders_Success() throws Exception {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTrainScheduleId(1L);
        order.setPassengerName("张三");
        order.setTicketCount(2);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setStatus(1);

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order));

        // When & Then
        mockMvc.perform(get("/admin/order")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data[0].passengerName").value("张三"));
    }

    @Test
    void updateOrder_Success() throws Exception {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(2); // 已完成

        when(orderService.updateOrder(any(Order.class))).thenReturn(order);

        // When & Then
        mockMvc.perform(put("/admin/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":2}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data.status").value(2));
    }

    @Test
    void deleteOrder_Success() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/admin/order/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS));
    }
}

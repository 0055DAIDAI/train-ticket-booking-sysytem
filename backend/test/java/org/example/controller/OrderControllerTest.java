package org.example.controller;

import org.example.BaseSpringBootTest;
import org.example.pojo.OrderVO;
import org.example.pojo.Result;
import org.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
class OrderControllerTest extends BaseSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void getOrdersByUserId_Success() throws Exception {
        // Given
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(1L);
        orderVO.setTrainNo("G123");
        orderVO.setFromStation("北京");
        orderVO.setToStation("上海");
        orderVO.setTravelDate(LocalDate.now());
        orderVO.setDepartureTime(LocalDateTime.now());
        orderVO.setArrivalTime(LocalDateTime.now().plusHours(5));
        orderVO.setPassengerName("张三");
        orderVO.setTicketCount(2);
        orderVO.setTotalPrice(new BigDecimal("200.00"));
        orderVO.setStatus(1);

        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(orderVO));

        // When & Then
        mockMvc.perform(get("/order/list?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Result.SUCCESS))
                .andExpect(jsonPath("$.data[0].orderId").value(1L))
                .andExpect(jsonPath("$.data[0].trainNo").value("G123"));
    }
}

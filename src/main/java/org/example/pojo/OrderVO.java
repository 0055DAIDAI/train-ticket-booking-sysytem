package org.example.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private Long orderId;
    private String trainNo;
    private String fromStation;
    private String toStation;
    private LocalDate travelDate;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String passengerName;
    private Integer ticketCount;
    private BigDecimal totalPrice;
    private Integer status;
}

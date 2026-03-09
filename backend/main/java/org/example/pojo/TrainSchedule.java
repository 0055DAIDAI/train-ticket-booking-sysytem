package org.example.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TrainSchedule {
    private Long id;
    private String trainNumber;
    private LocalDate date;
    private Long routeId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer ticketTotal;
    private Integer ticketSold;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private BigDecimal price;
}

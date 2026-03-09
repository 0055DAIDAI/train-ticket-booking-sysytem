package org.example.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TrainScheduleVO {
    private long id;
    private String trainNo;
    private String startTime;
    private String endTime;
    private String fromStation;
    private String toStation;
    private String duration;
    private BigDecimal price;
    private LocalDate date;
}

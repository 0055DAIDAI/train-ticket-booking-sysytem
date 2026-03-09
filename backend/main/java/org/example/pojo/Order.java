package org.example.pojo;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private Long trainScheduleId;
    private String passengerName;
    private Integer ticketCount;
    private BigDecimal totalPrice;
    private Integer status; // 1: 已出票, 2: 已完成, 3: 已取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

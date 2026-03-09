package org.example.request;
import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private Long trainScheduleId;
    private String passengerName;
    private Integer ticketCount;
}
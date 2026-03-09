package org.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Stop {
    private Long id;
    private long routeId;
    private long stationId;
    private long sequence;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
}

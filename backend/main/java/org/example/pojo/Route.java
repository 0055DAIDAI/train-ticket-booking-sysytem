package org.example.pojo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Route {
    private long id;
    private String name;
    private String fromStationName;
    private String toStationName;
}

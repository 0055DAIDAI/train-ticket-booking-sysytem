// TrainScheduleService.java
package org.example.service;

import org.example.pojo.TrainSchedule;

import java.util.List;

public interface TrainScheduleService {
    List<TrainSchedule> getAllSchedules();
    TrainSchedule addSchedule(TrainSchedule schedule);
    TrainSchedule updateSchedule( TrainSchedule schedule);
    void deleteSchedule(Long id);
}

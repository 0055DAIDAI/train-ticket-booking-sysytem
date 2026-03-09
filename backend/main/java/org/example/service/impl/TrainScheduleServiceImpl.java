// TrainScheduleServiceImpl.java
package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.TrainScheduleMapper;
import org.example.pojo.TrainSchedule;
import org.example.service.TrainScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TrainScheduleServiceImpl implements TrainScheduleService {

    @Autowired
    private TrainScheduleMapper trainScheduleMapper;

    @Override
    public List<TrainSchedule> getAllSchedules() {
        return trainScheduleMapper.findAll();
    }

    @Override
    public TrainSchedule addSchedule(TrainSchedule schedule) {

        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        schedule.setTicketSold(0);
        trainScheduleMapper.insert(schedule);
        return schedule;
    }

    @Override
    public TrainSchedule updateSchedule(TrainSchedule schedule) {
        TrainSchedule existing = trainScheduleMapper.findById(schedule.getId());
        if (existing == null) {
            throw new RuntimeException("车次不存在");
        }
        // 更新必要字段
        existing.setTrainNumber(schedule.getTrainNumber());
        existing.setDate(schedule.getDate());
        existing.setRouteId(schedule.getRouteId());
        existing.setDepartureTime(schedule.getDepartureTime());
        existing.setArrivalTime(schedule.getArrivalTime());
        existing.setTicketTotal(schedule.getTicketTotal());
        existing.setPrice(schedule.getPrice());
        existing.setUpdateTime(LocalDateTime.now());

        trainScheduleMapper.update(existing);
        return existing;
    }

    @Override
    public void deleteSchedule(Long id) {
        TrainSchedule existing = trainScheduleMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("车次不存在");
        }
        trainScheduleMapper.deleteById(id);
    }
}

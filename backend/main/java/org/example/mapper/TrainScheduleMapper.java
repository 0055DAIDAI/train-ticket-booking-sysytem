package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.TrainSchedule;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TrainScheduleMapper {

    List<TrainSchedule> findByRouteIdAndDate(Long routeId, LocalDate date, Integer page, Integer size);

    TrainSchedule findById(Long id);

    Integer countAll(long routeId,LocalDate date);

    List<TrainSchedule> findAll();
    void insert(TrainSchedule schedule);
    void update(TrainSchedule schedule);
    void deleteById(Long id);

}

// 文件路径：src/main/java/org/example/service/impl/RouteServiceImpl.java
package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.RouteMapper;
import org.example.mapper.StationMapper;
import org.example.mapper.StopMapper;
import org.example.mapper.TrainScheduleMapper;
import org.example.pojo.Route;
import org.example.pojo.Station;
import org.example.pojo.Stop;
import org.example.pojo.TrainSchedule;
import org.example.pojo.TrainScheduleVO;
import org.example.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteMapper routeMapper;
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private StopMapper stopMapper;
    @Autowired
    private TrainScheduleMapper trainScheduleMapper;

    @Override
    public List<TrainScheduleVO> searchRoutes(String from, String to, Integer page, Integer size, LocalDate date) {
        List<TrainScheduleVO> list = new ArrayList<>();
        if (from == null && to == null){
            // 1. 找到所有 route
            List<Route> routes = routeMapper.findAll();
            if (routes == null || routes.isEmpty()) {
                log.info("没有找到任何线路");
                return list;
            }
            for (Route route : routes) {
                // 2. 获取该 route 的所有 train_schedule 记录
                if (date == null){
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(route.getId(), LocalDate.now(),page,size);
                    if (trainSchedules == null || trainSchedules.isEmpty()) {
                        continue;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);
                    }
                    continue;

                }else {
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(route.getId(), date,page,size);
                    if (trainSchedules == null || trainSchedules.isEmpty()){
                        continue;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);
                    }
                    continue;
                }
            }
        } else if ( from != null && to != null){
            if (date == null) {
                List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByFromAndTo(from,to).getId(), LocalDate.now(),page,size);
                if (trainSchedules == null || trainSchedules.isEmpty()) {
                    return list;
                }
                for (TrainSchedule trainSchedule : trainSchedules) {
                    Route route = routeMapper.findByFromAndTo(from,to);
                    TrainScheduleVO vo = new TrainScheduleVO();
                    vo.setId(trainSchedule.getId());
                    vo.setTrainNo(trainSchedule.getTrainNumber());
                    vo.setFromStation(route.getFromStationName());
                    vo.setToStation(route.getToStationName());
                    vo.setPrice(trainSchedule.getPrice());
                    vo.setDate(trainSchedule.getDate());
                    vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                    list.add(vo);
                }
            }else{
                List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByFromAndTo(from,to).getId(), date,page,size);
                if (trainSchedules == null || trainSchedules.isEmpty()) {
                    log.info("没有找到任何线路");
                    return list;
                }
                for (TrainSchedule trainSchedule : trainSchedules) {
                    Route route = routeMapper.findByFromAndTo(from,to);
                    log.info("搜索结果：{}", route);
                    TrainScheduleVO vo = new TrainScheduleVO();
                    vo.setId(trainSchedule.getId());
                    vo.setTrainNo(trainSchedule.getTrainNumber());
                    vo.setFromStation(route.getFromStationName());
                    vo.setToStation(route.getToStationName());
                    vo.setPrice(trainSchedule.getPrice());
                    vo.setDate(trainSchedule.getDate());
                    vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                    list.add(vo);
                    log.info("搜索结果：{}", list);
                }
            }

        }else {
            if (from != null){
                if (date == null) {
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByFromName(from).getId(), LocalDate.now(), page, size);
                    if (trainSchedules == null || trainSchedules.isEmpty()) {
                        return list;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        Route route = routeMapper.findByFromAndTo(from,to);
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);
                    }
                }else {
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByFromName(from).getId(), date, page, size);
                    if (trainSchedules == null || trainSchedules.isEmpty()) {
                        return list;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        Route route = routeMapper.findByFromAndTo(from,to);
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);

                    }
                }
            }else {
                if (date == null){
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByToName(to).getId(), LocalDate.now(), page, size);
                    if (trainSchedules == null || trainSchedules.isEmpty()) {
                        return list;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        Route route = routeMapper.findByFromAndTo(from,to);
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);
                    }
                }else {
                    List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(routeMapper.findByToName(to).getId(), date, page, size);
                    if (trainSchedules == null || trainSchedules.isEmpty()) {
                        return list;
                    }
                    for (TrainSchedule trainSchedule : trainSchedules) {
                        Route route = routeMapper.findByFromAndTo(from,to);
                        TrainScheduleVO vo = new TrainScheduleVO();
                        vo.setId(trainSchedule.getId());
                        vo.setTrainNo(trainSchedule.getTrainNumber());
                        vo.setFromStation(route.getFromStationName());
                        vo.setToStation(route.getToStationName());
                        vo.setPrice(trainSchedule.getPrice());
                        vo.setDate(trainSchedule.getDate());
                        vo.setStartTime(trainSchedule.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setEndTime(trainSchedule.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
                        list.add(vo);
                    }
                }

            }

        }
        // 实现服务层分页
        if (page != null  && page > 0 ) {
            if (size ==  null){
                size = 5;
            }
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, list.size());

            if (startIndex >= list.size()) {
                log.info("搜索结果：{}", list);
                return new ArrayList<>(); // 返回空列表
            }

            log.info("搜索结果：{}", list);
            log.info("搜索结果：{}", list.subList(startIndex, endIndex));
            return list.subList(startIndex, endIndex);
        }


        log.info("搜索结果：{}", list);
        return list;

    }

    @Override
    public TrainScheduleVO getRouteDetail(Integer id) {
        Route route = routeMapper.findById(id);
        if (route == null) return null;

        LocalDate date = LocalDate.now();

        Integer page = 1;
        Integer size = 5;

        // 查询该路线的所有列车时刻信息
        List<TrainSchedule> trainSchedules = trainScheduleMapper.findByRouteIdAndDate(route.getId(),date,page,size);
        if (trainSchedules == null || trainSchedules.isEmpty()) return null;

        // 获取第一个列车时刻作为详情
        TrainSchedule trainSchedule = trainSchedules.get(0);

        // 获取首末站点信息
        List<Stop> stops = stopMapper.findByRouteId(route.getId());
        if (stops == null || stops.size() < 2) return null;

        stops.sort(Comparator.comparing(Stop::getSequence));
        Stop first = stops.get(0);
        Stop last = stops.get(stops.size() - 1);

        Station fromStationObj = stationMapper.findById(first.getStationId());
        Station toStationObj = stationMapper.findById(last.getStationId());
        if (fromStationObj == null || toStationObj == null) return null;

        TrainScheduleVO vo = new TrainScheduleVO();
        vo.setId(route.getId());
        vo.setTrainNo(trainSchedule.getTrainNumber());
        vo.setFromStation(fromStationObj.getStationName());
        vo.setToStation(toStationObj.getStationName());
        vo.setStartTime(trainSchedule.getDepartureTime().toString());
        vo.setEndTime(trainSchedule.getArrivalTime().toString());
        vo.setDuration(calculateDuration(trainSchedule.getDepartureTime(), trainSchedule.getArrivalTime()));
        vo.setPrice(BigDecimal.valueOf(80 + route.getId() * 20));

        return vo;
    }

    @Override
    public Integer countTrains(String from, String to, LocalDate date) {

        Route route = routeMapper.findByFromAndTo(from, to);
        if (date == null){
            date = LocalDate.now();
        }
        if (route == null){
            return trainScheduleMapper.countAll(-1, date);
        }else{
            return trainScheduleMapper.countAll(route.getId(), date);
        }

    }

    @Override
    public List<Route> getAllRoutes() {
        return routeMapper.findAll();
    }

    @Override
    public Route findById(Long id) {
        return routeMapper.findById(id);
    }

    @Override
    public Route addRoute(Route route) {
        // 这里需要在 RouteMapper 中添加 insert 方法
        routeMapper.insert(route);
        return route;
    }

    @Override
    public Route updateRoute(Route route) {
        // 先检查路线是否存在
        Route existingRoute = routeMapper.findById(route.getId());
        if (existingRoute == null) {
            throw new RuntimeException("路线不存在");
        }

        // 更新路线信息
        existingRoute.setFromStationName(route.getFromStationName());
        existingRoute.setToStationName(route.getToStationName());
        // 更新其他字段...

        routeMapper.update(existingRoute);
        return existingRoute;
    }

    @Override
    public void deleteRoute(Long id) {
        // 先检查路线是否存在
        Route existingRoute = routeMapper.findById(id);
        if (existingRoute == null) {
            throw new RuntimeException("路线不存在");
        }

        // 删除路线
        routeMapper.deleteById(id);
    }


    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        long minutes = Duration.between(start, end).toMinutes();
        long hours = minutes / 60;
        long mins = minutes % 60;
        return hours + "h" + mins + "m";
    }
}

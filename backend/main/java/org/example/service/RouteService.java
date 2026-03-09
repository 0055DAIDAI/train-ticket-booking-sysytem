package org.example.service;

import org.example.pojo.Route;
import org.example.pojo.TrainScheduleVO;

import java.time.LocalDate;
import java.util.List;



public interface RouteService {

    List<TrainScheduleVO> searchRoutes(String from, String to, Integer page, Integer size, LocalDate date);

    TrainScheduleVO getRouteDetail(Integer id);

    Integer countTrains(String from, String to, LocalDate date);

    List<Route> getAllRoutes();

    Route findById(Long id);

    Route addRoute(Route route);

    Route updateRoute(Route route);

    void deleteRoute(Long id);
}

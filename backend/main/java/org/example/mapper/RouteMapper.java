package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Route;
import org.example.pojo.Station;

import java.util.List;

@Mapper
public interface RouteMapper {

    List<Route> findAll();

    Route findById(long id);

    Route findByFromName(String from);

    Route findByToName(String to);

    Route findByFromAndTo(String from, String to);

    // 新增管理方法
    int insert(Route route);

    int update(Route route);

    int delete(long id);

    void deleteById(Long id);

}

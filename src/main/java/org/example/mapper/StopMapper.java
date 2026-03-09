package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Stop;

import java.util.List;

@Mapper
public interface StopMapper {

    List<Stop> findByRouteId(long routeId);
}

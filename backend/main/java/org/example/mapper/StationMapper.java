package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Station;

@Mapper
public interface StationMapper {

    Station findById(long id);


}

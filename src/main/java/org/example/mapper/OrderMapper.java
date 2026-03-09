package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.Order;
import org.example.pojo.OrderVO;
import org.example.pojo.Route;

import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Order order);
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);
    Order selectById(Long id);
    void update(Order order);

    // 修改 findAll 方法，添加分页参数
    List<Route> findAll(@Param("from") String from,
                        @Param("to") String to,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    // 添加查询总数的方法
    int countAll(@Param("from") String from,
                 @Param("to") String to);

    /**
     * 获取所有订单
     */
    List<Order> findAllWithNoPagination();

    /**
     * 根据ID删除订单
     */
    void deleteById(Long id);

}

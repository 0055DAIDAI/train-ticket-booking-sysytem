package org.example.service;

import org.example.pojo.Order;
import org.example.pojo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 创建订单
     */
    void createOrder(Long userId, Long trainScheduleId, String passengerName, Integer ticketCount);

    /**
     * 根据用户ID查询订单列表
     */
    List<OrderVO> getOrdersByUserId(Long userId);

    /**
     * 根据订单ID查询订单详情
     */
    OrderVO getOrderById(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 获取所有订单列表
     */
    List<Order> getAllOrders();

    /**
     * 更新订单
     */
    Order updateOrder(Order order);

    /**
     * 删除订单
     */

    void deleteOrder(Long orderId);

}

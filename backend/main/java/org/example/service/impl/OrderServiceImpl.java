// 文件路径：src/main/java/org/example/service/impl/OrderServiceImpl.java
package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.example.mapper.OrderMapper;
import org.example.mapper.RouteMapper;
import org.example.mapper.TrainScheduleMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.*;

import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TrainScheduleMapper trainScheduleMapper;

    @Autowired
    private RouteMapper routeMapper;

    @Transactional
    @Override
    public void createOrder(Long userId,Long trainScheduleId,String passengerName,Integer ticketCount) {
        // 1. 参数验证
        if (userId == null || trainScheduleId == null || passengerName == null || ticketCount == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (ticketCount <= 0) {
            throw new IllegalArgumentException("购票数量必须大于0");
        }

        // 2. 验证车次是否存在
        var schedule = trainScheduleMapper.findById(trainScheduleId);
        if (schedule == null) {
            log.warn("尝试创建订单时，车次不存在:{}", trainScheduleId);
            throw new RuntimeException("车次不存在");
        }

        // 3. 验证库存是否充足
        Integer count = Math.max(1,ticketCount);
        int availableTickets = schedule.getTicketTotal() - schedule.getTicketSold();
        if (count > availableTickets) {
            log.warn("库存不足，可用票数: {}, 请求票数:{}", availableTickets, count);
            throw new RuntimeException("库存不足");
        }

        // 4. 计算总价
        BigDecimal total = schedule.getPrice().multiply(BigDecimal.valueOf(count));

        // 5. 检查用户余额是否足够
        var user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getBalance().compareTo(total) < 0) {
            log.warn("用户余额不足，用户ID:{}, 余额:{}, 需金额:{}", userId, user.getBalance(), total);
            throw new RuntimeException("用户余额不足");
        }

        user.setBalance(user.getBalance().subtract(total));
        userMapper.update(user);

        // 6. 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setTrainScheduleId(trainScheduleId);
        order.setPassengerName(passengerName);
        order.setTicketCount(count);
        order.setTotalPrice(total);
        order.setStatus(1); // 1 = 已出票
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("成功创建订单，订单ID:{}", order.getId());
    }

    @Override
    public List<OrderVO> getOrdersByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        List<OrderVO> orderVOS = new ArrayList<>();
//      通过orderMapper.selectOrdersByUserId(userId)返回主要信息,
        List<Order> orders = orderMapper.selectOrdersByUserId(userId);

        if (orders == null || orders.isEmpty()){
            return orderVOS;
        }

        for (Order order : orders){
            OrderVO orderVO = new OrderVO();
            orderVO.setOrderId(order.getId());
            //      再通过返回的信息中的trainScheduleId获取车次信息,补充到OrderVO中的信息
            TrainSchedule schedule = trainScheduleMapper.findById(order.getTrainScheduleId());
            if (schedule == null) {
                // 如果找不到车次信息，跳过这个订单或设置默认值
                continue;
            }
            orderVO.setTrainNo(schedule.getTrainNumber());
            Route route = routeMapper.findById(schedule.getRouteId());
            if (route == null) {
                // 如果找不到路线信息，跳过这个订单或设置默认值
                continue;
            }
            orderVO.setFromStation(route.getFromStationName());
            orderVO.setToStation(route.getToStationName());
            orderVO.setArrivalTime(schedule.getArrivalTime());
            orderVO.setDepartureTime(schedule.getDepartureTime());
            orderVO.setTravelDate(schedule.getDate());
            orderVO.setPassengerName(order.getPassengerName());
            orderVO.setTicketCount(order.getTicketCount());
            orderVO.setTotalPrice(order.getTotalPrice());
            orderVO.setStatus(order.getStatus());
            orderVOS.add(orderVO);
        }



        return orderVOS;
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 创建 OrderVO 对象并设置所有字段
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getId());

        // 获取车次信息（需要从 train_schedule 表获取）
        var trainSchedule = trainScheduleMapper.findById(order.getTrainScheduleId());
        if (trainSchedule != null) {
            orderVO.setTrainNo(trainSchedule.getTrainNumber());

            // 获取车站信息（需要从 station 表获取）
            // 这里需要根据实际的数据库结构进行调整
            // 假设 train_schedule 表中有 from_station_id 和 to_station_id 字段
            // var fromStation = stationMapper.findById(trainSchedule.getFromStationId());
            // var toStation = stationMapper.findById(trainSchedule.getToStationId());
            // orderVO.setFromStation(fromStation.getStationName());
            // orderVO.setToStation(toStation.getStationName());
            // orderVO.setTravelDate(trainSchedule.getDate().toString());
            // orderVO.setDepartureTime(trainSchedule.getDepartureTime().toString());
            // orderVO.setArrivalTime(trainSchedule.getArrivalTime().toString());
        }

        orderVO.setPassengerName(order.getPassengerName());
        orderVO.setTicketCount(order.getTicketCount());
        orderVO.setTotalPrice(order.getTotalPrice());

        // 设置状态文本
        switch (order.getStatus()) {
            case 1:
                orderVO.setStatus(1);
                break;
            case 2:
                orderVO.setStatus(2);
                break;
            case 3:
                orderVO.setStatus(0);
                break;
            default:
                orderVO.setStatus(-1);
        }

        return orderVO;
    }


    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() == 3) {
            throw new RuntimeException("订单已取消");
        }

        // 更新订单状态为已取消
        order.setStatus(3); // 3: 已取消
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.update(order);

        // 返还票数
        TrainSchedule trainSchedule = trainScheduleMapper.findById(order.getTrainScheduleId());
        if (trainSchedule != null) {
            trainSchedule.setTicketSold(trainSchedule.getTicketSold() - order.getTicketCount());
            trainScheduleMapper.update(trainSchedule);
        }

        // 返还用户余额
        User user = userMapper.findById(order.getUserId());
        if (user != null) {
            user.setBalance(user.getBalance().add(order.getTotalPrice()));
            userMapper.update(user);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        // 需要在 OrderMapper 中实现 findAll 方法
        return orderMapper.findAllWithNoPagination();
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order existingOrder = orderMapper.selectById(order.getId());
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        // 处理票数变更逻辑
        handleTicketChange(existingOrder, order);

        // 更新订单的所有可变字段
        existingOrder.setUserId(order.getUserId());
        existingOrder.setTrainScheduleId(order.getTrainScheduleId());
        existingOrder.setPassengerName(order.getPassengerName());
        existingOrder.setTicketCount(order.getTicketCount());
        existingOrder.setTotalPrice(order.getTotalPrice());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setUpdateTime(LocalDateTime.now());

        orderMapper.update(existingOrder);
        return existingOrder;
    }

    /**
     * 处理票数变更逻辑
     */
    private void handleTicketChange(Order existingOrder, Order newOrder) {
        // 如果订单状态从"已出票"变为"已取消"，需要返还票数
        if (existingOrder.getStatus() == 1 && newOrder.getStatus() == 3) {
            TrainSchedule trainSchedule = trainScheduleMapper.findById(existingOrder.getTrainScheduleId());
            if (trainSchedule != null) {
                // 返还票数
                trainSchedule.setTicketSold(trainSchedule.getTicketSold() - existingOrder.getTicketCount());
                trainScheduleMapper.update(trainSchedule);
            }
        }
        // 如果订单状态从"已取消"变为"已出票"，需要扣减票数
        else if (existingOrder.getStatus() == 3 && newOrder.getStatus() == 1) {
            TrainSchedule trainSchedule = trainScheduleMapper.findById(newOrder.getTrainScheduleId());
            if (trainSchedule != null) {
                // 扣减票数
                int availableTickets = trainSchedule.getTicketTotal() - trainSchedule.getTicketSold();
                if (newOrder.getTicketCount() > availableTickets) {
                    throw new RuntimeException("库存不足");
                }
                trainSchedule.setTicketSold(trainSchedule.getTicketSold() + newOrder.getTicketCount());
                trainScheduleMapper.update(trainSchedule);
            }
        }
        // 如果只是票数变更，需要调整票数差额
        else if (existingOrder.getStatus() == 1 && newOrder.getStatus() == 1 &&
                !existingOrder.getTicketCount().equals(newOrder.getTicketCount())) {
            TrainSchedule trainSchedule = trainScheduleMapper.findById(existingOrder.getTrainScheduleId());
            if (trainSchedule != null) {
                int ticketDiff = newOrder.getTicketCount() - existingOrder.getTicketCount();
                int availableTickets = trainSchedule.getTicketTotal() - trainSchedule.getTicketSold();

                if (ticketDiff > 0 && ticketDiff > availableTickets) {
                    throw new RuntimeException("库存不足");
                }

                trainSchedule.setTicketSold(trainSchedule.getTicketSold() + ticketDiff);
                trainScheduleMapper.update(trainSchedule);
            }
        }
    }

    @Override
    public void deleteOrder(Long id) {
        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        orderMapper.deleteById(id);
    }

}

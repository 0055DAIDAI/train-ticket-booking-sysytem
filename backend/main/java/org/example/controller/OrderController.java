// 文件路径：src/main/java/org/example/controller/OrderController.java
package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.OrderVO;
import org.example.pojo.Result;
import org.example.request.OrderRequest;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Void> createOrder(@RequestBody OrderRequest request) {
        try {
            orderService.createOrder(request.getUserId(), request.getTrainScheduleId(),
                    request.getPassengerName(), request.getTicketCount());
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建订单失败:{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrdersByUserId(Long userId) {
        List<OrderVO> orders = orderService.getOrdersByUserId(userId);
        return Result.success(orders);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderById(@PathVariable Long orderId) {
        OrderVO order = orderService.getOrderById(orderId);
        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消订单失败:{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}

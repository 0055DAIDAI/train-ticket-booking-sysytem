package org.example.controller;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.*;
import org.example.service.AdminService;
import org.example.service.OrderService;
import org.example.service.RouteService;
import org.example.service.TrainScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private TrainScheduleService trainScheduleService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/login")
    public Result<AdminLoginInfo> login(@RequestBody LoginRequest request) {
        log.info("管理员登录:adminName={}", request.getAdminName());
        try {
            AdminLoginInfo loginInfo = adminService.login(request.getAdminName(), request.getPassword());
            if (loginInfo != null) {
                adminService.updateLastLogin(loginInfo.getId(), LocalDateTime.now());
            }
            return Result.success(loginInfo);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public Result<Admin> register(@RequestBody AdminRegisterRequest request) {
        try {
            // 创建 Admin 对象
            Admin admin = new Admin();
            admin.setAdminName(request.getAdminName());
            admin.setPassword(request.getPassword());

            Admin registeredAdmin = adminService.register(admin);
            // 安全：不返回密码
            registeredAdmin.setPassword(null);
            return Result.success(registeredAdmin);
        } catch (Exception e) {
            log.error("管理员注册失败: {}", e.getMessage());
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    @GetMapping("/route")
    public Result<List<Route>> getAllRoutes() {
        // 需要在 RouteService 中添加 getAllRoutes 方法
        List<Route> routes = routeService.getAllRoutes();
        return Result.success(routes);
    }

    @GetMapping("/route/{id}")
    public Result<Route> findById(@PathVariable Long id) {
        Route route = routeService.findById(id);
        if (route == null) {
            return Result.error("路线不存在");
        }
        return Result.success(route);
    }

    /**
     * 添加路线
     */
    @PostMapping("/route")
    public Result<Route> addRoute(@RequestBody Route route) {
        // 需要在 RouteService 中添加 addRoute 方法
        Route savedRoute = routeService.addRoute(route);
        return Result.success(savedRoute);
    }

    /**
     * 更新路线
     */
    @PutMapping("/route")
    public Result<Route> updateRoute(@RequestBody Route route) {
        // 需要在 RouteService 中添加 updateRoute 方法
        Route updatedRoute = routeService.updateRoute(route);
        return Result.success(updatedRoute);
    }

    /**
     * 删除路线
     */
    @DeleteMapping("/route/{id}")
    public Result<Void> deleteRoute(@PathVariable Long id) {
        // 需要在 RouteService 中添加 deleteRoute 方法
        routeService.deleteRoute(id);
        return Result.success();
    }

    @GetMapping("/station")
    public Result<List<Station>> getAllStations() {
        // 需要添加 StationService 并实现相关方法
        return Result.success(null);
    }

    /**
     * 添加站台
     */
    @PostMapping("/station")
    public Result<Station> addStation(@RequestBody Station station) {
        // 需要添加 StationService 并实现相关方法
        return Result.success(null);
    }

    /**
     * 更新站台
     */
    @PutMapping("/station/{id}")
    public Result<Station> updateStation(@PathVariable Long id, @RequestBody Station station) {
        // 需要添加 StationService 并实现相关方法
        return Result.success(null);
    }

    /**
     * 删除站台
     */
    @DeleteMapping("/station/{id}")
    public Result<Void> deleteStation(@PathVariable Long id) {
        // 需要添加 StationService 并实现相关方法
        return Result.success();
    }


    @GetMapping("/schedule")
    public Result<List<TrainSchedule>> getAllSchedules() {
        List<TrainSchedule> schedules = trainScheduleService.getAllSchedules();
        return Result.success(schedules);
    }

    /**
     * 添加车次
     */
    @PostMapping("/schedule")
    public Result<TrainSchedule> addSchedule(@RequestBody TrainSchedule schedule) {
        try {
            TrainSchedule savedSchedule = trainScheduleService.addSchedule(schedule);
            return Result.success(savedSchedule);
        } catch (Exception e) {
            log.error("添加车次失败: {}", e.getMessage());
            return Result.error("添加车次失败: " + e.getMessage());
        }
    }

    /**
     * 更新车次
     */
    @PutMapping("/schedule")
    public Result<TrainSchedule> updateSchedule(@RequestBody TrainSchedule schedule) {
        try {
            // 确保 schedule.getId() 不为空
            if (schedule.getId() == null) {
                return Result.error("车次ID不能为空");
            }
            TrainSchedule updatedSchedule = trainScheduleService.updateSchedule(schedule);
            return Result.success(updatedSchedule);
        } catch (Exception e) {
            log.error("更新车次失败: {}", e.getMessage());
            return Result.error("更新车次失败: " + e.getMessage());
        }
    }

    /**
     * 删除车次
     */
    @DeleteMapping("/schedule/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        try {
            trainScheduleService.deleteSchedule(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除车次失败: {}", e.getMessage());
            return Result.error("删除车次失败: " + e.getMessage());
        }
    }

    @GetMapping("/order")
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    @PutMapping("/order")
    public Result<Order> updateOrder(@RequestBody Order order) {
        try {
            Order updatedOrder = orderService.updateOrder(order);
            return Result.success(updatedOrder);
        } catch (Exception e) {
            log.error("更新订单失败: {}", e.getMessage());
            return Result.error("更新订单失败: " + e.getMessage());
        }
    }
    @DeleteMapping("/order/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除订单失败: {}", e.getMessage());
            return Result.error("删除订单失败: " + e.getMessage());
        }
    }





    @Data
    public static class LoginRequest {
        private String adminName;
        private String password;
    }

    @Data
    public static class AdminRegisterRequest {
        private String adminName;
        private String password;
    }

}

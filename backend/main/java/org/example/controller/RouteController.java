package org.example.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.RouteMapper;
import org.example.pojo.PageResult;
import org.example.pojo.Result;
import org.example.pojo.TrainScheduleVO;
import org.example.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    /**
     * 搜索线路（用于首页列表）
     */
    @PostMapping("/search")
    public Result<PageResult<TrainScheduleVO>> searchRoutes(@RequestBody SearchRequest request) {
        // 默认每页5条数据
        int pageSize = 5;
        // 如果请求中没有指定页码，默认为第1页
        int pageNum = request.getPage() == 0 ? 1 : request.getPage();

        // 获取当前页的数据
        List<TrainScheduleVO> result = routeService.searchRoutes(
                request.getFrom(),
                request.getTo(),
                pageNum,
                pageSize,
                request.getDate()

        );

        long total = routeService.countTrains(
                request.getFrom(),
                request.getTo(),
                request.getDate()
        );

        log.info("搜索结果：{}", result);

        PageResult<TrainScheduleVO> pageResult = new PageResult<>(result, total, pageNum, pageSize);
        return Result.success(pageResult);
    }


    /**
     * 获取某条线路的详细停靠信息
     */
    @GetMapping("/{id}/detail")
    public Result<TrainScheduleVO> getRouteDetail(@PathVariable Integer id) {
        TrainScheduleVO detail = routeService.getRouteDetail(id);
        return Result.success(detail);
    }

    @Data
    private static class SearchRequest {
        private String from;
        private String to;
        private Integer page;
        private LocalDate date;

    }
}

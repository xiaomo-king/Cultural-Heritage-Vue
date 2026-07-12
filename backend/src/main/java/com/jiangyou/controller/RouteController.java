package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.service.RouteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/route")
public class RouteController {
    private final RouteService routeService;
    public RouteController(RouteService rs) { this.routeService = rs; }

    @GetMapping("/list")
    public ApiResponse<?> getList(@RequestParam(required = false) String city) {
        return ApiResponse.success(routeService.getList(city));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getDetail(@PathVariable Long id) {
        Map<String, Object> data = routeService.getDetail(id);
        return data != null ? ApiResponse.success(data) : ApiResponse.error("路线不存在");
    }
}
package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.model.Route;
import com.jiangyou.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/route")
public class RouteAdminController {

    private final RouteService routeService;

    public RouteAdminController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/list")
    public ApiResponse<List<Route>> list() {
        return ApiResponse.success(routeService.adminList());
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> data = routeService.getDetail(id);
        return data != null ? ApiResponse.success(data) : ApiResponse.error("路线不存在");
    }

    @PostMapping("/create")
    public ApiResponse<Route> create(@RequestBody Map<String, Object> body) {
        if (body.get("title") == null || ((String) body.get("title")).isEmpty()) {
            return ApiResponse.error("路线名称不能为空");
        }
        Route route = routeService.create(body);
        // 保存路线节点
        if (body.get("points") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> points = (List<Map<String, Object>>) body.get("points");
            if (!points.isEmpty()) {
                routeService.savePoints(route.getId(), points);
            }
        }
        return ApiResponse.success(route);
    }

    @PostMapping("/update")
    public ApiResponse<Route> update(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null) return ApiResponse.error("ID不能为空");
        Long id = ((Number) body.get("id")).longValue();
        Route route = routeService.update(id, body);
        if (route == null) return ApiResponse.error("路线不存在");
        // 更新路线节点
        if (body.get("points") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> points = (List<Map<String, Object>>) body.get("points");
            routeService.savePoints(route.getId(), points);
        }
        return ApiResponse.success(route);
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        return routeService.delete(id) ? ApiResponse.success("删除成功", null) : ApiResponse.error("路线不存在");
    }
}

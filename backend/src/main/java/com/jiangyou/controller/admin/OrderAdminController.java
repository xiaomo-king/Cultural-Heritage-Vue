package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Order;
import com.jiangyou.model.OrderItem;
import com.jiangyou.repository.OrderItemRepository;
import com.jiangyou.repository.OrderRepository;
import com.jiangyou.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/order")
public class OrderAdminController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderAdminController(OrderService orderService, OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Map<String, Object>>> list(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> result;
        if (status != null && !"all".equals(status)) {
            result = orderRepository.findByStatusOrderByCreatedAtDesc(status, PageRequest.of(page, size));
        } else {
            result = orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        }
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : result.getContent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("order", order);
            map.put("items", orderItemRepository.findByOrderId(order.getId()));
            orderList.add(map);
        }
        return ApiResponse.success(new PageResponse<>(orderList, page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ApiResponse.error("订单不存在");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order", order);
        data.put("items", orderItemRepository.findByOrderId(id));
        return ApiResponse.success(data);
    }

    @PostMapping("/ship")
    public ApiResponse<?> ship(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null) return ApiResponse.error("订单ID不能为空");
        Long id = ((Number) body.get("id")).longValue();
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ApiResponse.error("订单不存在");
        if (!"paid".equals(order.getStatus())) return ApiResponse.error("订单状态不是待发货");
        order.setStatus("shipped");
        order.setShippedAt(LocalDateTime.now());
        order.setExpressCompany(body.get("expressCompany") != null ? (String) body.get("expressCompany") : "");
        order.setExpressNumber(body.get("expressNumber") != null ? (String) body.get("expressNumber") : "");
        orderRepository.save(order);
        return ApiResponse.success("发货成功", null);
    }

    @PostMapping("/status")
    public ApiResponse<?> updateStatus(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null || body.get("status") == null) {
            return ApiResponse.error("参数不完整");
        }
        Long id = ((Number) body.get("id")).longValue();
        String status = (String) body.get("status");
        try {
            orderService.updateStatus(id, status);
            return ApiResponse.success("状态更新成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

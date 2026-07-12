package com.jiangyou.controller;

import com.jiangyou.dto.*;
import com.jiangyou.model.Order;
import com.jiangyou.model.OrderItem;
import com.jiangyou.model.Product;
import com.jiangyou.repository.OrderItemRepository;
import com.jiangyou.repository.OrderRepository;
import com.jiangyou.repository.ProductRepository;
import com.jiangyou.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    public OrderController(OrderService os, OrderItemRepository oir, ProductRepository pr, OrderRepository or) { this.orderService = os; this.orderItemRepository = oir; this.productRepository = pr; this.orderRepository = or; }

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestHeader("userId") Long userId, @RequestBody CreateOrderRequest req) {
        Order order = orderService.createOrder(userId, req);
        return ApiResponse.success("下单成功", Map.of("orderId", order.getId(), "orderNo", order.getOrderNo()));
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Map<String, Object>>> getList(
            @RequestHeader("userId") Long userId,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> result = orderService.getOrderList(userId, status, PageRequest.of(page, size));
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : result.getContent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order);
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            map.put("items", items);
            orderList.add(map);
        }
        return ApiResponse.success(new PageResponse<>(orderList, page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        if (order == null) return ApiResponse.error("订单不存在");
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        return ApiResponse.success(data);
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<?> pay(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        try {
            Order order = orderService.payOrder(id, userId);
            return ApiResponse.success("支付成功", Map.of("status", order.getStatus()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Order order = orderService.updateStatus(id, body.get("status"));
            return ApiResponse.success(Map.of("status", order.getStatus()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<?> getOrderCount(@RequestHeader("userId") Long userId) {
        Map<String, Object> counts = new HashMap<>();
        counts.put("all", orderRepository.countByUserId(userId));
        counts.put("pending", orderRepository.countByUserIdAndStatus(userId, "pending"));
        counts.put("paid", orderRepository.countByUserIdAndStatus(userId, "paid"));
        counts.put("shipped", orderRepository.countByUserIdAndStatus(userId, "shipped"));
        counts.put("received", orderRepository.countByUserIdAndStatus(userId, "received"));
        return ApiResponse.success(counts);
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<Map<String, Object>>> search(
            @RequestHeader("userId") Long userId,
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> result;
        if (status != null && !"all".equals(status)) {
            result = orderService.searchOrdersByStatus(userId, status, keyword, PageRequest.of(page, size));
        } else {
            result = orderService.searchOrders(userId, keyword, PageRequest.of(page, size));
        }
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : result.getContent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("order", order);
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            map.put("items", items);
            orderList.add(map);
        }
        return ApiResponse.success(new PageResponse<>(orderList, page, size, result.getTotalElements()));
    }

    @PostMapping("/{id}/evaluate")
    public ApiResponse<?> evaluate(@PathVariable Long id, @RequestBody EvaluationRequest req) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) return ApiResponse.error("订单不存在");
            order.setEvaluated(1);
            orderRepository.save(order);
            if (req.getProductId() != null && req.getRating() != null) {
                Product p = productRepository.findById(req.getProductId()).orElse(null);
                if (p != null) {
                    productRepository.updateRating(req.getProductId(), req.getRating());
                }
            }
            return ApiResponse.success("评价成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
package com.jiangyou.service;

import com.jiangyou.dto.CreateOrderRequest;
import com.jiangyou.model.*;
import com.jiangyou.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    public OrderService(OrderRepository or, OrderItemRepository oir, ProductRepository pr,
                        AddressRepository ar, UserRepository ur, CartService cs) {
        this.orderRepository = or; this.orderItemRepository = oir;
        this.productRepository = pr; this.addressRepository = ar; this.userRepository = ur;
        this.cartService = cs;
    }

    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest req) {
        Order order = new Order();
        order.setOrderNo("JY" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setStatus("pending");
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.CartItem item : req.getItems()) {
            Product p = productRepository.findById(item.getProductId()).orElse(null);
            if (p != null) {
                BigDecimal specPrice = cartService.getSpecPrice(p, item.getSpec());
                BigDecimal unitPrice = specPrice != null ? specPrice : p.getPrice();
                total = total.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        order.setTotalAmount(total);
        if (req.getAddressId() != null) {
            Address addr = addressRepository.findById(req.getAddressId()).orElse(null);
            if (addr != null) {
                order.setConsignee(addr.getConsignee());
                order.setPhone(addr.getPhone());
                order.setAddressRegion(addr.getRegion());
                order.setAddressDetail(addr.getDetail());
            }
        }
        if (req.getRemark() != null) order.setRemark(req.getRemark());

        // 先保存获取ID
        Order savedOrder = orderRepository.save(order);

        for (CreateOrderRequest.CartItem item : req.getItems()) {
            Product p = productRepository.findById(item.getProductId()).orElse(null);
            if (p == null) continue;
            OrderItem oi = new OrderItem();
            oi.setOrderId(savedOrder.getId());
            oi.setProductId(item.getProductId());
            oi.setProductName(p.getName());
            oi.setProductImage(p.getImages() != null ? p.getImages().split(",")[0] : "");
            // 使用规格对应的价格
            BigDecimal specPrice = cartService.getSpecPrice(p, item.getSpec());
            oi.setPrice(specPrice != null ? specPrice : p.getPrice());
            oi.setQuantity(item.getQuantity());
            oi.setSpec(item.getSpec() != null ? item.getSpec() : "");
            orderItemRepository.save(oi);
            p.setSales(p.getSales() + item.getQuantity());
            productRepository.save(p);
        }
        return savedOrder;
    }

    public Page<Order> getOrderList(Long userId, String status, Pageable pageable) {
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Order> searchOrders(Long userId, String keyword, Pageable pageable) {
        return orderRepository.searchByUserId(userId, keyword, pageable);
    }

    public Page<Order> searchOrdersByStatus(Long userId, String status, String keyword, Pageable pageable) {
        return orderRepository.searchByUserIdAndStatus(userId, status, keyword, pageable);
    }

    public Order getOrderDetail(Long orderId) { return orderRepository.findById(orderId).orElse(null); }

    @Transactional
    public Order payOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!"pending".equals(order.getStatus())) throw new RuntimeException("订单状态不正确");
        // 检查是否超时（30分钟）
        if (order.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            order.setStatus("cancelled");
            orderRepository.save(order);
            throw new RuntimeException("订单已超时取消");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) throw new RuntimeException("余额不足");
        user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
        userRepository.save(user);
        order.setStatus("paid");
        order.setPaidAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    // 自动取消30分钟未支付的订单
    @Transactional
    public int autoCancelExpiredOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);
        List<Order> expired = orderRepository.findByStatusAndCreatedAtBefore("pending", deadline);
        for (Order order : expired) {
            order.setStatus("cancelled");
            orderRepository.save(order);
        }
        return expired.size();
    }

    @Transactional
    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new RuntimeException("订单不存在");
        order.setStatus(status);
        if ("shipped".equals(status)) order.setShippedAt(LocalDateTime.now());
        if ("received".equals(status)) order.setReceivedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}

package com.jiangyou.service;

import com.jiangyou.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final HeritageRepository heritageRepository;
    private final ProductRepository productRepository;
    private final PostRepository postRepository;
    private final OrderRepository orderRepository;

    public DashboardService(UserRepository userRepository, HeritageRepository heritageRepository,
                            ProductRepository productRepository, PostRepository postRepository,
                            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.heritageRepository = heritageRepository;
        this.productRepository = productRepository;
        this.postRepository = postRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 1. 概览数据
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("userCount", userRepository.count());
        overview.put("heritageCount", heritageRepository.count());
        overview.put("productCount", productRepository.count());
        overview.put("postCount", postRepository.count());
        overview.put("orderCount", orderRepository.count());

        // 计算总收入（只算已支付/已发货/已收货/已完成的订单）
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        overview.put("totalRevenue", totalRevenue);
        data.put("overview", overview);

        // 2. 用户注册趋势（近7天）
        List<Map<String, Object>> userTrend = new ArrayList<>();
        for (Object[] row : userRepository.getUserRegistrationTrend()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", row[0] != null ? row[0].toString() : "");
            item.put("count", row[1] != null ? ((Number) row[1]).intValue() : 0);
            userTrend.add(item);
        }
        data.put("userTrend", userTrend);

        // 3. 非遗分类分布
        List<Map<String, Object>> categoryDistribution = new ArrayList<>();
        for (Object[] row : heritageRepository.countByCategory()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", row[0] != null ? row[0].toString() : "");
            item.put("count", row[1] != null ? ((Number) row[1]).intValue() : 0);
            categoryDistribution.add(item);
        }
        data.put("categoryDistribution", categoryDistribution);

        // 4. 热门非遗排行
        List<Map<String, Object>> topHeritage = new ArrayList<>();
        for (Object[] row : heritageRepository.findTopHeritageByCheckinCount()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", row[0] != null ? ((Number) row[0]).longValue() : 0);
            item.put("name", row[1] != null ? row[1].toString() : "");
            item.put("coverImage", row[2] != null ? row[2].toString() : "");
            item.put("checkinCount", row[3] != null ? ((Number) row[3]).intValue() : 0);
            topHeritage.add(item);
        }
        data.put("topHeritage", topHeritage);

        // 5. 每日订单趋势（近30天）
        List<Map<String, Object>> orderTrend = new ArrayList<>();
        for (Object[] row : orderRepository.getDailyOrderTrend()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", row[0] != null ? row[0].toString() : "");
            item.put("orderCount", row[1] != null ? ((Number) row[1]).intValue() : 0);
            item.put("amount", row[2] != null ? ((Number) row[2]) : BigDecimal.ZERO);
            orderTrend.add(item);
        }
        data.put("orderTrend", orderTrend);

        // 6. 订单状态分布
        List<Map<String, Object>> orderStatusDistribution = new ArrayList<>();
        for (Object[] row : orderRepository.countByStatus()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", row[0] != null ? row[0].toString() : "");
            item.put("count", row[1] != null ? ((Number) row[1]).intValue() : 0);
            orderStatusDistribution.add(item);
        }
        data.put("orderStatusDistribution", orderStatusDistribution);

        return data;
    }
}

package com.jiangyou.config;

import com.jiangyou.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelTask {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelTask.class);
    private final OrderService orderService;

    public OrderCancelTask(OrderService orderService) {
        this.orderService = orderService;
    }

    // 每分钟检查一次过期未支付的订单（30分钟超时）
    @Scheduled(fixedRate = 60000)
    public void cancelExpiredOrders() {
        try {
            int count = orderService.autoCancelExpiredOrders();
            if (count > 0) {
                log.info("已自动取消 {} 个超时未支付订单", count);
            }
        } catch (Exception e) {
            log.error("自动取消订单失败", e);
        }
    }
}

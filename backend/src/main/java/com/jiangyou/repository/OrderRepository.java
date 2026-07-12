package com.jiangyou.repository;
import com.jiangyou.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status, Pageable pageable);
    int countByUserIdAndStatus(Long userId, String status);
    int countByUserId(Long userId);
    List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);

    // 管理端查询
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Order> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @Query(value = "SELECT DISTINCT o.* FROM orders o JOIN order_item oi ON o.id = oi.order_id WHERE o.user_id = ?1 AND oi.product_name LIKE %?2% ORDER BY o.created_at DESC",
           countQuery = "SELECT COUNT(DISTINCT o.id) FROM orders o JOIN order_item oi ON o.id = oi.order_id WHERE o.user_id = ?1 AND oi.product_name LIKE %?2%",
           nativeQuery = true)
    Page<Order> searchByUserId(Long userId, String keyword, Pageable pageable);
    @Query(value = "SELECT DISTINCT o.* FROM orders o JOIN order_item oi ON o.id = oi.order_id WHERE o.user_id = ?1 AND o.status = ?2 AND oi.product_name LIKE %?3% ORDER BY o.created_at DESC",
           countQuery = "SELECT COUNT(DISTINCT o.id) FROM orders o JOIN order_item oi ON o.id = oi.order_id WHERE o.user_id = ?1 AND o.status = ?2 AND oi.product_name LIKE %?3%",
           nativeQuery = true)
    Page<Order> searchByUserIdAndStatus(Long userId, String status, String keyword, Pageable pageable);

    // 仪表盘统计
    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN ('paid', 'shipped', 'received', 'completed')", nativeQuery = true)
    BigDecimal getTotalRevenue();
    @Query(value = "SELECT DATE(created_at) AS dateStr, COUNT(*) AS orderCount, COALESCE(SUM(total_amount), 0) AS amount FROM orders WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(created_at) ORDER BY dateStr", nativeQuery = true)
    List<Object[]> getDailyOrderTrend();
    @Query(value = "SELECT status, COUNT(*) AS cnt FROM orders GROUP BY status", nativeQuery = true)
    List<Object[]> countByStatus();
}
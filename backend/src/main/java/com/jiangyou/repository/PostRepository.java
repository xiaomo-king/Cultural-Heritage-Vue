package com.jiangyou.repository;
import com.jiangyou.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Integer status, Pageable pageable);
    Page<Post> findByTopicAndStatus(String topic, Integer status, Pageable pageable);
    Page<Post> findByHeritageIdAndStatus(Long heritageId, Integer status, Pageable pageable);
    // 按可见性筛选
    Page<Post> findByVisibilityAndStatusOrderByCreatedAtDesc(String visibility, Integer status, Pageable pageable);
    Page<Post> findByTopicAndVisibilityAndStatus(String topic, String visibility, Integer status, Pageable pageable);
    Page<Post> findByUserIdAndVisibilityAndStatusOrderByCreatedAtDesc(Long userId, String visibility, Integer status, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND p.visibility = 'public' AND (p.content LIKE %?1% OR p.heritageName LIKE %?1% OR p.topic LIKE %?1% OR p.tags LIKE %?1%)")
    Page<Post> search(String keyword, Pageable pageable);

    // 管理端查询
    List<Post> findByUserId(Long userId);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT DATE(created_at) AS dateStr, COUNT(*) AS cnt FROM post WHERE status = 1 AND created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(created_at) ORDER BY dateStr", nativeQuery = true)
    List<Object[]> getPostTrend();
}
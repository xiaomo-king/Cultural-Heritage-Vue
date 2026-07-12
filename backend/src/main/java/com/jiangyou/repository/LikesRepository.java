package com.jiangyou.repository;
import com.jiangyou.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    int countByPostId(Long postId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    List<Likes> findByUserId(Long userId);
}
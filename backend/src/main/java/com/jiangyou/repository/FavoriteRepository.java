package com.jiangyou.repository;
import com.jiangyou.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    boolean existsByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    List<Favorite> findByUserIdAndTargetType(Long userId, String targetType);
    void deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
}
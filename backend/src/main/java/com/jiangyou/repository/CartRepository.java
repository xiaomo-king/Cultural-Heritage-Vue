package com.jiangyou.repository;
import com.jiangyou.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndProductIdAndSpec(Long userId, Long productId, String spec);
    void deleteByUserId(Long userId);
}
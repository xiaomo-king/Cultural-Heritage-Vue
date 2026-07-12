package com.jiangyou.repository;
import com.jiangyou.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
    Page<Product> findByHeritageIdAndStatus(Long heritageId, Integer status, Pageable pageable);
    Page<Product> findByStatus(Integer status, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (p.name LIKE %?1% OR p.tags LIKE %?1%)")
    Page<Product> search(String keyword, Pageable pageable);

    // 管理端查询（含已下架的）
    Page<Product> findAllByOrderByIdAsc(Pageable pageable);
    Page<Product> findByCategoryOrderByIdAsc(String category, Pageable pageable);
    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.rating = (p.rating * p.sales + ?2) / (p.sales + 1) WHERE p.id = ?1")
    void updateRating(Long productId, Integer rating);
}
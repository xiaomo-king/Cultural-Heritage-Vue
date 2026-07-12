package com.jiangyou.repository;
import com.jiangyou.model.Heritage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface HeritageRepository extends JpaRepository<Heritage, Long> {
    Page<Heritage> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
    Page<Heritage> findByStatus(Integer status, Pageable pageable);
    List<Heritage> findByCityAndStatus(String city, Integer status);

    // 管理端查询（含已下架的，按ID正序）
    Page<Heritage> findAllByOrderByIdAsc(Pageable pageable);
    Page<Heritage> findByCategoryOrderByIdAsc(String category, Pageable pageable);
    List<Heritage> findByNameContaining(String keyword);

    @Query("SELECT h FROM Heritage h WHERE h.status = 1 AND (h.name LIKE %?1% OR h.tags LIKE %?1% OR h.city LIKE %?1% OR h.category LIKE %?1%)")
    Page<Heritage> search(String keyword, Pageable pageable);
    @Query(value = "SELECT * FROM heritage WHERE status = 1 AND latitude IS NOT NULL ORDER BY ST_Distance_Sphere(POINT(longitude, latitude), POINT(?2, ?1))", nativeQuery = true)
    List<Heritage> findNearby(double lat, double lng, Pageable pageable);
    @Query(value = "SELECT category, COUNT(*) AS cnt FROM heritage WHERE status = 1 GROUP BY category ORDER BY cnt DESC", nativeQuery = true)
    List<Object[]> countByCategory();
    @Query(value = "SELECT id, name, cover_image, checkin_count FROM heritage WHERE status = 1 ORDER BY checkin_count DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTopHeritageByCheckinCount();
}
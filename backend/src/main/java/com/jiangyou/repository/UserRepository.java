package com.jiangyou.repository;
import com.jiangyou.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOpenid(String openid);
    Optional<User> findByNickName(String nickName);
    Optional<User> findByAccount(String account);
    boolean existsByAccount(String account);
    boolean existsByNickName(String nickName);
    @Query("SELECT u FROM User u WHERE u.nickName LIKE %?1% AND u.status = 1")
    List<User> searchByNickName(String keyword);
    @Query(value = "SELECT DATE(join_date) AS dateStr, COUNT(*) AS cnt FROM user WHERE join_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(join_date) ORDER BY dateStr", nativeQuery = true)
    List<Object[]> getUserRegistrationTrend();

    // 管理端
    Page<User> findAllByOrderByJoinDateDesc(Pageable pageable);
    List<User> findByNickNameContaining(String keyword);

    // 手机号登录
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}

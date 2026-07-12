package com.jiangyou.repository;

import com.jiangyou.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByUsernameAndStatus(String username, Integer status);
    boolean existsByUsername(String username);
}

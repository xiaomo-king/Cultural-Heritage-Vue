package com.jiangyou.service;

import com.jiangyou.config.JwtUtil;
import com.jiangyou.model.AdminUser;
import com.jiangyou.repository.AdminUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminUserRepository adminUserRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(AdminUserRepository adminUserRepository, JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 启动时自动创建默认管理员（如果不存在）
     */
    @PostConstruct
    public void initDefaultAdmin() {
        if (!adminUserRepository.existsByUsername("admin")) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("系统管理员");
            adminUserRepository.save(admin);
            System.out.println("✅ 默认管理员已创建（admin / 123456）");
        }
    }

    /**
     * 管理员登录
     * @return 包含 token 和用户信息的 Map，登录失败返回 null
     */
    public Map<String, Object> login(String username, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByUsernameAndStatus(username, 1);
        if (adminOpt.isEmpty()) {
            return null;
        }

        AdminUser admin = adminOpt.get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return null;
        }

        // 更新最后登录时间
        admin.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(admin);

        // 生成 JWT Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        result.put("avatarUrl", admin.getAvatarUrl());
        return result;
    }
}

package com.jiangyou.service;

import com.jiangyou.dto.LoginResponse;
import com.jiangyou.model.User;
import com.jiangyou.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    // 注册（账号+密码+手机号）
    public LoginResponse register(String account, String password, String phone) {
        // 后端校验
        if (account == null || !account.matches("^[a-zA-Z0-9]{8,15}$")) {
            throw new RuntimeException("账号需要8-15位数字或字母");
        }
        if (password == null || !password.matches("^[a-zA-Z0-9]{6,20}$")) {
            throw new RuntimeException("密码需要6-20位数字或字母");
        }
        if (userRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在");
        }
        // 手机号校验
        if (phone != null && !phone.isEmpty()) {
            if (!phone.matches("^1[34578]\\d{9}$")) {
                throw new RuntimeException("手机号格式不正确");
            }
            if (userRepository.findByPhone(phone).isPresent()) {
                throw new RuntimeException("该手机号已被注册");
            }
        }
        // 生成默认昵称：用户+4位随机数字（确保唯一）
        String defaultNickName;
        do {
            defaultNickName = "用户" + String.format("%04d", (int)(Math.random() * 10000));
        } while (userRepository.existsByNickName(defaultNickName));

        User user = new User();
        user.setAccount(account);
        user.setOpenid("pwd_" + UUID.randomUUID().toString().substring(0, 8));
        user.setNickName(defaultNickName);
        user.setPassword(password);
        if (phone != null) user.setPhone(phone);
        user.setBalance(BigDecimal.ZERO);
        user.setJoinDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);
        return buildLoginResponse(user);
    }

    // 登录（账号/手机号 + 密码）
    public LoginResponse login(String account, String password) {
        // 先按账号查找
        User user = userRepository.findByAccount(account).orElse(null);
        // 如果没找到，尝试按手机号查找
        if (user == null) {
            user = userRepository.findByPhone(account).orElse(null);
        }
        if (user == null) {
            throw new RuntimeException("账号或手机号不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);
        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        LoginResponse resp = new LoginResponse();
        resp.setUserId(user.getId());
        resp.setOpenid(user.getOpenid());
        resp.setToken("token_" + user.getId());
        resp.setNickName(user.getNickName());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setBalance(user.getBalance());
        return resp;
    }

    public User getUserById(Long id) { return userRepository.findById(id).orElse(null); }

    public BigDecimal getBalance(Long userId) {
        return userRepository.findById(userId).map(User::getBalance).orElse(BigDecimal.ZERO);
    }

    // 搜索用户（按昵称）
    public List<Map<String, Object>> searchUsers(String keyword) {
        List<User> users = userRepository.searchByNickName(keyword);
        return users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("nickName", u.getNickName());
            m.put("avatarUrl", u.getAvatarUrl());
            m.put("bio", u.getBio());
            m.put("followCount", u.getFollowCount());
            m.put("followerCount", u.getFollowerCount());
            m.put("checkinCount", u.getCheckinCount());
            return m;
        }).collect(Collectors.toList());
    }

    public BigDecimal recharge(Long userId, BigDecimal amount) {
        return userRepository.findById(userId).map(user -> {
            user.setBalance(user.getBalance().add(amount));
            userRepository.save(user);
            return user.getBalance();
        }).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 更新个人信息（昵称、性别、简介、所在地、头像）
    public User updateProfile(Long userId, String nickName, String avatarUrl, String gender, String bio, String location) {
        return userRepository.findById(userId).map(user -> {
            if (nickName != null) user.setNickName(nickName);
            if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
            if (gender != null) user.setGender(gender);
            if (bio != null) user.setBio(bio);
            if (location != null) user.setLocation(location);
            return userRepository.save(user);
        }).orElse(null);
    }

    // ===== 管理端方法 =====
    public Page<User> adminList(int page, int size) {
        return userRepository.findAllByOrderByJoinDateDesc(PageRequest.of(page, size));
    }

    public boolean toggleStatus(Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setStatus(user.getStatus() == 1 ? 0 : 1);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}

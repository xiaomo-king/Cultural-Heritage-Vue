package com.jiangyou.controller;

import com.jiangyou.dto.*;
import com.jiangyou.model.User;
import com.jiangyou.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody LoginRequest req) {
        try {
            LoginResponse resp = userService.register(req.getAccount(), req.getPassword(), req.getPhone());
            return ApiResponse.success("注册成功", resp);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse resp = userService.login(req.getAccount(), req.getPassword());
            return ApiResponse.success(resp);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ApiResponse.success(user) : ApiResponse.error("用户不存在");
    }

    @GetMapping("/{id}/balance")
    public ApiResponse<java.math.BigDecimal> getBalance(@PathVariable Long id) {
        return ApiResponse.success(userService.getBalance(id));
    }

    @GetMapping("/search")
    public ApiResponse<?> search(@RequestParam String keyword) {
        return ApiResponse.success(userService.searchUsers(keyword));
    }

    @PostMapping("/{id}/recharge")
    public ApiResponse<?> recharge(@PathVariable Long id, @RequestBody RechargeRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ApiResponse.error("金额必须大于0");
        }
        java.math.BigDecimal balance = userService.recharge(id, req.getAmount());
        return ApiResponse.success("充值成功", java.util.Map.of("balance", balance));
    }

    @PutMapping("/{id}/profile")
    public ApiResponse<User> updateProfile(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        User user = userService.updateProfile(id,
                body.get("nickName"),
                body.get("avatarUrl"),
                body.get("gender"),
                body.get("bio"),
                body.get("location"));
        return user != null ? ApiResponse.success(user) : ApiResponse.error("用户不存在");
    }
}
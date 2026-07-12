package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员登录（无需 JWT 鉴权）
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return ApiResponse.error("密码不能为空");
        }

        Map<String, Object> result = adminService.login(username, password);
        if (result == null) {
            return ApiResponse.error("用户名或密码错误");
        }
        return ApiResponse.success(result);
    }
}

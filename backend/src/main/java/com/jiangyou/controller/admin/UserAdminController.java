package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.User;
import com.jiangyou.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<User>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> result = userService.adminList(page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/search")
    public ApiResponse<?> search(@RequestParam String keyword) {
        List<Map<String, Object>> users = userService.searchUsers(keyword);
        return ApiResponse.success(users);
    }

    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ApiResponse.success(user) : ApiResponse.error("用户不存在");
    }

    @PostMapping("/toggle-status/{id}")
    public ApiResponse<?> toggleStatus(@PathVariable Long id) {
        return userService.toggleStatus(id) ? ApiResponse.success("操作成功", null) : ApiResponse.error("用户不存在");
    }
}

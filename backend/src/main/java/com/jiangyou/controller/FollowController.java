package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.model.User;
import com.jiangyou.repository.UserRepository;
import com.jiangyou.service.FollowService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;
    private final UserRepository userRepository;
    public FollowController(FollowService fs, UserRepository ur) { this.followService = fs; this.userRepository = ur; }

    @PostMapping("/toggle")
    public ApiResponse<?> toggle(@RequestHeader(value = "userId", required = false) Long userId, @RequestBody Map<String, Long> body) {
        Long targetId = body.get("targetUserId");
        if (targetId == null) return ApiResponse.error("参数错误");
        if (userId == null) return ApiResponse.error("请先登录");
        boolean following = followService.toggleFollow(userId, targetId);
        return ApiResponse.success(Map.of("isFollowing", following));
    }

    @GetMapping("/check")
    public ApiResponse<?> check(@RequestHeader(value = "userId", required = false) Long userId, @RequestParam Long targetUserId) {
        if (userId == null) return ApiResponse.success(Map.of("isFollowing", false));
        return ApiResponse.success(Map.of("isFollowing", followService.isFollowing(userId, targetUserId)));
    }

    @GetMapping("/{userId}/followings")
    public ApiResponse<List<Map<String, Object>>> getFollowings(@PathVariable Long userId) {
        List<Long> ids = followService.getFollowings(userId);
        List<Map<String, Object>> users = userRepository.findAllById(ids).stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId()); m.put("nickName", u.getNickName()); m.put("avatarUrl", u.getAvatarUrl());
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(users);
    }

    @GetMapping("/{userId}/followers")
    public ApiResponse<List<Map<String, Object>>> getFollowers(@PathVariable Long userId) {
        List<Long> ids = followService.getFollowers(userId);
        List<Map<String, Object>> users = userRepository.findAllById(ids).stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId()); m.put("nickName", u.getNickName()); m.put("avatarUrl", u.getAvatarUrl());
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(users);
    }
}
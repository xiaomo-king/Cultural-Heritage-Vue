package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.model.Favorite;
import com.jiangyou.service.FavoriteService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;
    public FavoriteController(FavoriteService fs) { this.favoriteService = fs; }

    @PostMapping("/toggle")
    public ApiResponse<?> toggle(@RequestHeader("userId") Long userId, @RequestBody Map<String, Object> body) {
        Long targetId = Long.valueOf(body.get("targetId").toString());
        String targetType = body.get("targetType").toString();
        boolean favorited = favoriteService.toggleFavorite(userId, targetId, targetType);
        return ApiResponse.success(Map.of("isFavorited", favorited));
    }

    @GetMapping("/list")
    public ApiResponse<List<Favorite>> getList(@RequestHeader("userId") Long userId, @RequestParam String targetType) {
        return ApiResponse.success(favoriteService.getFavorites(userId, targetType));
    }

    @GetMapping("/check")
    public ApiResponse<?> check(@RequestHeader("userId") Long userId, @RequestParam Long targetId, @RequestParam String targetType) {
        return ApiResponse.success(Map.of("isFavorited", favoriteService.isFavorited(userId, targetId, targetType)));
    }
}
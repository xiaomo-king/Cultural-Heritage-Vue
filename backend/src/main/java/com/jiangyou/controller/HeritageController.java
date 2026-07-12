package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Heritage;
import com.jiangyou.service.HeritageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/heritage")
public class HeritageController {
    private final HeritageService heritageService;
    public HeritageController(HeritageService heritageService) { this.heritageService = heritageService; }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Heritage>> getList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Heritage> result = heritageService.getList(category, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Heritage> getDetail(@PathVariable Long id) {
        Heritage h = heritageService.getDetail(id);
        return h != null ? ApiResponse.success(h) : ApiResponse.error("非遗项目不存在");
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<Heritage>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Heritage> result = heritageService.search(keyword, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/nearby")
    public ApiResponse<List<Heritage>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(heritageService.getNearby(lat, lng, limit));
    }
}
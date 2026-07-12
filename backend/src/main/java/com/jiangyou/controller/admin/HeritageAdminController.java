package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Heritage;
import com.jiangyou.service.HeritageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/heritage")
public class HeritageAdminController {

    private final HeritageService heritageService;

    public HeritageAdminController(HeritageService heritageService) {
        this.heritageService = heritageService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Heritage>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Heritage> result = heritageService.adminList(category, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Heritage> detail(@PathVariable Long id) {
        Heritage h = heritageService.getDetail(id);
        return h != null ? ApiResponse.success(h) : ApiResponse.error("非遗项目不存在");
    }

    @PostMapping("/create")
    public ApiResponse<Heritage> create(@RequestBody Map<String, Object> body) {
        if (body.get("name") == null || ((String) body.get("name")).isEmpty()) {
            return ApiResponse.error("名称不能为空");
        }
        if (body.get("category") == null || ((String) body.get("category")).isEmpty()) {
            return ApiResponse.error("分类不能为空");
        }
        return ApiResponse.success(heritageService.create(body));
    }

    @PostMapping("/update")
    public ApiResponse<Heritage> update(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null) return ApiResponse.error("ID不能为空");
        Long id = ((Number) body.get("id")).longValue();
        Heritage h = heritageService.update(id, body);
        return h != null ? ApiResponse.success(h) : ApiResponse.error("非遗项目不存在");
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        return heritageService.delete(id) ? ApiResponse.success("删除成功", null) : ApiResponse.error("非遗项目不存在");
    }
}

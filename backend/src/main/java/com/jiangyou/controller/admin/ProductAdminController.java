package com.jiangyou.controller.admin;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.dto.PageResponse;
import com.jiangyou.model.Product;
import com.jiangyou.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/product")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Product>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productService.adminList(category, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        Product p = productService.getDetail(id);
        return p != null ? ApiResponse.success(p) : ApiResponse.error("商品不存在");
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<Product>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productService.adminSearch(keyword, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @PostMapping("/create")
    public ApiResponse<Product> create(@RequestBody Map<String, Object> body) {
        if (body.get("name") == null || ((String) body.get("name")).isEmpty()) {
            return ApiResponse.error("商品名称不能为空");
        }
        return ApiResponse.success(productService.create(body));
    }

    @PostMapping("/update")
    public ApiResponse<Product> update(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null) return ApiResponse.error("ID不能为空");
        Long id = ((Number) body.get("id")).longValue();
        Product p = productService.update(id, body);
        return p != null ? ApiResponse.success(p) : ApiResponse.error("商品不存在");
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        return productService.delete(id) ? ApiResponse.success("删除成功", null) : ApiResponse.error("商品不存在");
    }
}

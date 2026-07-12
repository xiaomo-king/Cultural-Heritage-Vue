package com.jiangyou.controller;

import com.jiangyou.dto.*;
import com.jiangyou.model.Product;
import com.jiangyou.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Product>> getList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long heritageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productService.getList(category, heritageId, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getDetail(@PathVariable Long id) {
        Product p = productService.getDetail(id);
        return p != null ? ApiResponse.success(p) : ApiResponse.error("商品不存在");
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<Product>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productService.search(keyword, page, size);
        return ApiResponse.success(new PageResponse<>(result.getContent(), page, size, result.getTotalElements()));
    }
}
package com.jiangyou.controller;

import com.jiangyou.dto.ApiResponse;
import com.jiangyou.model.Cart;
import com.jiangyou.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> getList(@RequestHeader("userId") Long userId) {
        return ApiResponse.success(cartService.getCartList(userId));
    }

    @PostMapping("/add")
    public ApiResponse<Cart> add(@RequestHeader("userId") Long userId, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = body.containsKey("quantity") ? Integer.valueOf(body.get("quantity").toString()) : 1;
        String spec = body.containsKey("spec") ? body.get("spec").toString() : "";
        Cart cart = cartService.addToCart(userId, productId, quantity, spec);
        return ApiResponse.success(cart);
    }

    @PostMapping("/update")
    public ApiResponse<?> update(@RequestBody Map<String, Object> body) {
        Long cartId = Long.valueOf(body.get("cartId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        Cart cart = cartService.updateQuantity(cartId, quantity);
        return ApiResponse.success(cart != null ? cart : "已删除");
    }

    @PostMapping("/remove")
    public ApiResponse<?> remove(@RequestBody Map<String, Object> body) {
        Long cartId = Long.valueOf(body.get("cartId").toString());
        cartService.removeFromCart(cartId);
        return ApiResponse.success("已删除", null);
    }

    @PostMapping("/clear")
    public ApiResponse<?> clear(@RequestHeader("userId") Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.success("已清空", null);
    }
}

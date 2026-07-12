package com.jiangyou.service;

import com.jiangyou.model.Cart;
import com.jiangyou.model.Product;
import com.jiangyou.repository.CartRepository;
import com.jiangyou.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cr, ProductRepository pr) {
        this.cartRepository = cr;
        this.productRepository = pr;
    }

    public List<Map<String, Object>> getCartList(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart c : carts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("userId", c.getUserId());
            item.put("productId", c.getProductId());
            item.put("quantity", c.getQuantity());
            item.put("spec", c.getSpec());
            item.put("createdAt", c.getCreatedAt());
            Product p = productRepository.findById(c.getProductId()).orElse(null);
            if (p != null) {
                item.put("productName", p.getName());
                item.put("productImage", p.getImages() != null ? p.getImages().split(",")[0] : "");
                // 根据规格获取对应价格
                java.math.BigDecimal specPrice = getSpecPrice(p, c.getSpec());
                item.put("price", specPrice != null ? specPrice : p.getPrice());
                item.put("stock", p.getStock());
                item.put("productStatus", p.getStatus());
            }
            result.add(item);
        }
        return result;
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity, String spec) {
        if (spec == null) spec = "";
        Optional<Cart> existing = cartRepository.findByUserIdAndProductIdAndSpec(userId, productId, spec);
        if (existing.isPresent()) {
            Cart cart = existing.get();
            cart.setQuantity(cart.getQuantity() + (quantity != null ? quantity : 1));
            return cartRepository.save(cart);
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity != null ? quantity : 1);
        cart.setSpec(spec);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) throw new RuntimeException("购物车商品不存在");
        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    // 从商品specs JSON中获取指定规格的价格
    public java.math.BigDecimal getSpecPrice(Product p, String spec) {
        if (spec == null || spec.isEmpty() || p.getSpecs() == null) return null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> specs = mapper.readValue(p.getSpecs(), Map.class);
            if (specs.containsKey("价格")) {
                Map<String, Object> priceMap = (Map<String, Object>) specs.get("价格");
                if (priceMap.containsKey(spec)) {
                    Object val = priceMap.get(spec);
                    if (val instanceof Number) {
                        return java.math.BigDecimal.valueOf(((Number) val).doubleValue());
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败就用默认价格
        }
        return null;
    }
}

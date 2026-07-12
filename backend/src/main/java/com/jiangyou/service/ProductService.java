package com.jiangyou.service;

import com.jiangyou.model.Product;
import com.jiangyou.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }

    public Page<Product> getList(String category, Long heritageId, int page, int size) {
        if (heritageId != null) return productRepository.findByHeritageIdAndStatus(heritageId, 1, PageRequest.of(page, size));
        if (category != null && !category.isEmpty()) return productRepository.findByCategoryAndStatus(category, 1, PageRequest.of(page, size));
        return productRepository.findByStatus(1, PageRequest.of(page, size));
    }

    public Product getDetail(Long id) { return productRepository.findById(id).orElse(null); }

    public Page<Product> search(String keyword, int page, int size) {
        return productRepository.search(keyword, PageRequest.of(page, size));
    }

    // ===== 管理端方法 =====

    public Page<Product> adminList(String category, int page, int size) {
        if (category != null && !category.isEmpty()) {
            return productRepository.findByCategoryOrderByIdAsc(category, PageRequest.of(page, size));
        }
        return productRepository.findAllByOrderByIdAsc(PageRequest.of(page, size));
    }

    public Page<Product> adminSearch(String keyword, int page, int size) {
        return productRepository.findByNameContaining(keyword, PageRequest.of(page, size));
    }

    @Transactional
    public Product create(Map<String, Object> body) {
        Product p = new Product();
        applyBodyToProduct(p, body);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(p);
    }

    @Transactional
    public Product update(Long id, Map<String, Object> body) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) return null;
        applyBodyToProduct(p, body);
        p.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(p);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }

    private void applyBodyToProduct(Product p, Map<String, Object> body) {
        if (body.get("name") != null) p.setName((String) body.get("name"));
        if (body.get("description") != null) p.setDescription((String) body.get("description"));
        if (body.get("price") != null) p.setPrice(new BigDecimal(body.get("price").toString()));
        if (body.get("originalPrice") != null) p.setOriginalPrice(new BigDecimal(body.get("originalPrice").toString()));
        if (body.get("stock") != null) p.setStock(((Number) body.get("stock")).intValue());
        if (body.get("images") != null) p.setImages((String) body.get("images"));
        if (body.get("specs") != null) p.setSpecs((String) body.get("specs"));
        if (body.get("category") != null) p.setCategory((String) body.get("category"));
        if (body.get("seller") != null) p.setSeller((String) body.get("seller"));
        if (body.get("sellerAvatar") != null) p.setSellerAvatar((String) body.get("sellerAvatar"));
        if (body.get("tags") != null) p.setTags((String) body.get("tags"));
        if (body.get("heritageId") != null) p.setHeritageId(((Number) body.get("heritageId")).longValue());
        if (body.get("sales") != null) p.setSales(((Number) body.get("sales")).intValue());
        if (body.get("rating") != null) {
            Object rating = body.get("rating");
            if (rating instanceof Number) {
                p.setRating(new BigDecimal(rating.toString()));
            }
        }
        if (body.get("status") != null) p.setStatus(((Number) body.get("status")).intValue());
    }
}

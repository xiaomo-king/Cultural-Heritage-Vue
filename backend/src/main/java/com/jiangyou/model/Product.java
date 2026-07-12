package com.jiangyou.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heritage_id")
    private Long heritageId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "original_price")
    private BigDecimal originalPrice = BigDecimal.ZERO;

    private Integer stock = 0;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(columnDefinition = "TEXT")
    private String specs;

    @Column(length = 32)
    private String category = "";

    @Column(length = 64)
    private String seller = "";

    @Column(name = "seller_avatar", length = 512)
    private String sellerAvatar = "";

    private Integer sales = 0;
    private java.math.BigDecimal rating = new java.math.BigDecimal("5.0");

    @Column(length = 256)
    private String tags = "";

    private Integer status = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getHeritageId() { return heritageId; } public void setHeritageId(Long heritageId) { this.heritageId = heritageId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; } public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getStock() { return stock; } public void setStock(Integer stock) { this.stock = stock; }
    public String getImages() { return images; } public void setImages(String images) { this.images = images; }
    public String getSpecs() { return specs; } public void setSpecs(String specs) { this.specs = specs; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public String getSeller() { return seller; } public void setSeller(String seller) { this.seller = seller; }
    public String getSellerAvatar() { return sellerAvatar; } public void setSellerAvatar(String sellerAvatar) { this.sellerAvatar = sellerAvatar; }
    public Integer getSales() { return sales; } public void setSales(Integer sales) { this.sales = sales; }
    public java.math.BigDecimal getRating() { return rating; } public void setRating(java.math.BigDecimal rating) { this.rating = rating; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

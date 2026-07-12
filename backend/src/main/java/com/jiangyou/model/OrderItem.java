package com.jiangyou.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name = "order_item")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 128)
    private String productName = "";

    @Column(name = "product_image", length = 512)
    private String productImage = "";

    @Column(nullable = false)
    private BigDecimal price;

    private Integer quantity = 1;

    @Column(length = 64)
    private String spec = "";

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; } public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getProductId() { return productId; } public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; } public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; } public void setProductImage(String productImage) { this.productImage = productImage; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getSpec() { return spec; } public void setSpec(String spec) { this.spec = spec; }
}

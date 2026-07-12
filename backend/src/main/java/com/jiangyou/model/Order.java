package com.jiangyou.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(length = 16)
    private String status = "pending";

    @Column(length = 32)
    private String consignee = "";

    @Column(length = 20)
    private String phone = "";

    @Column(name = "address_region", length = 64)
    private String addressRegion = "";

    @Column(name = "address_detail", length = 256)
    private String addressDetail = "";

    @Column(name = "express_company", length = 32)
    private String expressCompany = "";

    @Column(name = "express_number", length = 64)
    private String expressNumber = "";

    private Integer evaluated = 0;

    @Column(length = 256)
    private String remark = "";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; } public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getConsignee() { return consignee; } public void setConsignee(String consignee) { this.consignee = consignee; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getAddressRegion() { return addressRegion; } public void setAddressRegion(String addressRegion) { this.addressRegion = addressRegion; }
    public String getAddressDetail() { return addressDetail; } public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getExpressCompany() { return expressCompany; } public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }
    public String getExpressNumber() { return expressNumber; } public void setExpressNumber(String expressNumber) { this.expressNumber = expressNumber; }
    public Integer getEvaluated() { return evaluated; } public void setEvaluated(Integer evaluated) { this.evaluated = evaluated; }
    public String getRemark() { return remark; } public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; } public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getShippedAt() { return shippedAt; } public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; } public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
}

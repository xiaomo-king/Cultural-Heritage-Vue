package com.jiangyou.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name = "address")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(nullable = false, length = 32) private String consignee;
    @Column(nullable = false, length = 20) private String phone;
    @Column(length = 64) private String region = "";
    @Column(length = 256) private String detail = "";
    @Column(name = "is_default") private Integer isDefault = 0;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getConsignee() { return consignee; } public void setConsignee(String consignee) { this.consignee = consignee; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getRegion() { return region; } public void setRegion(String region) { this.region = region; }
    public String getDetail() { return detail; } public void setDetail(String detail) { this.detail = detail; }
    public Integer getIsDefault() { return isDefault; } public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
package com.jiangyou.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name = "favorite")
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "target_id", nullable = false) private Long targetId;
    @Column(name = "target_type", nullable = false, length = 16) private String targetType;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getTargetId() { return targetId; } public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getTargetType() { return targetType; } public void setTargetType(String targetType) { this.targetType = targetType; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
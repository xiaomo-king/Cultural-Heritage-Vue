package com.jiangyou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "likes")
public class Likes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; } public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

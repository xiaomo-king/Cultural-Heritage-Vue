package com.jiangyou.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name = "follow")
public class Follow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "follower_id", nullable = false) private Long followerId;
    @Column(name = "following_id", nullable = false) private Long followingId;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getFollowerId() { return followerId; } public void setFollowerId(Long followerId) { this.followerId = followerId; }
    public Long getFollowingId() { return followingId; } public void setFollowingId(Long followingId) { this.followingId = followingId; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
package com.jiangyou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "comment")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(name = "reply_to_user_id")
    private Long replyToUserId;
    @Column(name = "reply_to_content", length = 512)
    private String replyToContent = "";
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    // 非持久化字段，前端展示用
    @Transient private String nickName;
    @Transient private String avatarUrl;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; } public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public Long getReplyToUserId() { return replyToUserId; } public void setReplyToUserId(Long replyToUserId) { this.replyToUserId = replyToUserId; }
    public String getReplyToContent() { return replyToContent; } public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getNickName() { return nickName; } public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}

package com.jiangyou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "post")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "heritage_id")
    private Long heritageId;
    @Column(name = "heritage_name", length = 128)
    private String heritageName = "";
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String images;
    @Column(name = "location_name", length = 128)
    private String locationName = "";
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    @Column(length = 256)
    private String tags = "";
    @Column(length = 64)
    private String topic = "";
    @Column(length = 128)
    private String title = "";
    @Column(name = "like_count")
    private Integer likeCount = 0;
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    private Integer status = 1;
    @Column(length = 16)
    private String visibility = "public";
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public Long getHeritageId() { return heritageId; } public void setHeritageId(Long heritageId) { this.heritageId = heritageId; }
    public String getHeritageName() { return heritageName; } public void setHeritageName(String heritageName) { this.heritageName = heritageName; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public String getImages() { return images; } public void setImages(String images) { this.images = images; }
    public String getLocationName() { return locationName; } public void setLocationName(String locationName) { this.locationName = locationName; }
    public java.math.BigDecimal getLatitude() { return latitude; } public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }
    public java.math.BigDecimal getLongitude() { return longitude; } public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public String getTopic() { return topic; } public void setTopic(String topic) { this.topic = topic; }
    public Integer getLikeCount() { return likeCount; } public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; } public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
    public String getVisibility() { return visibility; } public void setVisibility(String visibility) { this.visibility = visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

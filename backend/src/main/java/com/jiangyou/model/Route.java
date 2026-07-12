package com.jiangyou.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name = "route")
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 128) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "cover_image", length = 512) private String coverImage = "";
    @Column(length = 32) private String duration = "";
    @Column(length = 32) private String city = "";
    @Column(length = 256) private String tags = "";
    private Integer status = 1;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; } public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getDuration() { return duration; } public void setDuration(String duration) { this.duration = duration; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
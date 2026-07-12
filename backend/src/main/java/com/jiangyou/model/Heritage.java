package com.jiangyou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "heritage")
public class Heritage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 128)
    private String alias = "";

    @Column(nullable = false, length = 32)
    private String category;

    @Column(length = 16)
    private String level = "";

    @Column(length = 32)
    private String batch = "";

    @Column(length = 32)
    private String city = "";

    @Column(length = 32)
    private String county = "";

    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String history;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(name = "cover_image", length = 512)
    private String coverImage = "";

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(length = 256)
    private String tags = "";

    @Column(name = "travel_tips", columnDefinition = "TEXT")
    private String travelTips;

    @Column(name = "visit_hours", length = 128)
    private String visitHours = "";

    @Column(name = "ticket_info", length = 256)
    private String ticketInfo = "";

    @Column(name = "checkin_count")
    private Integer checkinCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    private Integer status = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }
    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }
    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getTravelTips() { return travelTips; }
    public void setTravelTips(String travelTips) { this.travelTips = travelTips; }
    public String getVisitHours() { return visitHours; }
    public void setVisitHours(String visitHours) { this.visitHours = visitHours; }
    public String getTicketInfo() { return ticketInfo; }
    public void setTicketInfo(String ticketInfo) { this.ticketInfo = ticketInfo; }
    public Integer getCheckinCount() { return checkinCount; }
    public void setCheckinCount(Integer checkinCount) { this.checkinCount = checkinCount; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

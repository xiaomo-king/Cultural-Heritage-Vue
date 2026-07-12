package com.jiangyou.dto;
import java.util.List;
public class PostRequest {
    private String title = "";
    private Long heritageId;
    private String heritageName;
    private String content;
    private List<String> images;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private List<String> tags;
    private String topic;
    private String visibility;
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public Long getHeritageId() { return heritageId; } public void setHeritageId(Long heritageId) { this.heritageId = heritageId; }
    public String getHeritageName() { return heritageName; } public void setHeritageName(String heritageName) { this.heritageName = heritageName; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public List<String> getImages() { return images; } public void setImages(List<String> images) { this.images = images; }
    public String getLocationName() { return locationName; } public void setLocationName(String locationName) { this.locationName = locationName; }
    public Double getLatitude() { return latitude; } public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; } public void setLongitude(Double longitude) { this.longitude = longitude; }
    public List<String> getTags() { return tags; } public void setTags(List<String> tags) { this.tags = tags; }
    public String getTopic() { return topic; } public void setTopic(String topic) { this.topic = topic; }
    public String getVisibility() { return visibility; } public void setVisibility(String visibility) { this.visibility = visibility; }
}
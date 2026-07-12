package com.jiangyou.dto;
public class EvaluationRequest {
    private Long orderId;
    private Long productId;
    private Integer rating;
    private String content;
    public Long getOrderId() { return orderId; } public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getProductId() { return productId; } public void setProductId(Long productId) { this.productId = productId; }
    public Integer getRating() { return rating; } public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
}
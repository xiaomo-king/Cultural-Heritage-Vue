package com.jiangyou.dto;
public class CommentRequest {
    private String content;
    private Long replyToUserId;
    private String replyToContent;
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public Long getReplyToUserId() { return replyToUserId; } public void setReplyToUserId(Long replyToUserId) { this.replyToUserId = replyToUserId; }
    public String getReplyToContent() { return replyToContent; } public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
}
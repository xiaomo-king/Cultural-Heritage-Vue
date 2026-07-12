package com.jiangyou.dto;

import java.math.BigDecimal;

public class LoginResponse {
    private Long userId;
    private String openid;
    private String token;
    private String nickName;
    private String avatarUrl;
    private BigDecimal balance;
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getOpenid() { return openid; } public void setOpenid(String openid) { this.openid = openid; }
    public String getToken() { return token; } public void setToken(String token) { this.token = token; }
    public String getNickName() { return nickName; } public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public BigDecimal getBalance() { return balance; } public void setBalance(BigDecimal balance) { this.balance = balance; }
}
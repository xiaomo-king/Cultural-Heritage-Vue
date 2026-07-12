package com.jiangyou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String account;

    @Column(nullable = true, unique = true, length = 64)
    private String openid;

    @Column(length = 128)
    private String password = "";

    @Column(name = "nick_name", length = 64)
    private String nickName = "";

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl = "";

    @Column(length = 8)
    private String gender = "";

    @Column(length = 256)
    private String bio = "";

    @Column(length = 128)
    private String location = "";

    private java.math.BigDecimal balance = java.math.BigDecimal.ZERO;

    private String phone = "";

    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin = LocalDateTime.now();

    @Column(name = "follow_count")
    private Integer followCount = 0;

    @Column(name = "follower_count")
    private Integer followerCount = 0;

    @Column(name = "checkin_count")
    private Integer checkinCount = 0;

    private Integer status = 1;

    // Getters & Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getAccount() { return account; } public void setAccount(String account) { this.account = account; }
    public String getOpenid() { return openid; } public void setOpenid(String openid) { this.openid = openid; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getNickName() { return nickName; } public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
    public String getBio() { return bio; } public void setBio(String bio) { this.bio = bio; }
    public String getLocation() { return location; } public void setLocation(String location) { this.location = location; }
    public java.math.BigDecimal getBalance() { return balance; } public void setBalance(java.math.BigDecimal balance) { this.balance = balance; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getJoinDate() { return joinDate; } public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    public LocalDateTime getLastLogin() { return lastLogin; } public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public Integer getFollowCount() { return followCount; } public void setFollowCount(Integer followCount) { this.followCount = followCount; }
    public Integer getFollowerCount() { return followerCount; } public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }
    public Integer getCheckinCount() { return checkinCount; } public void setCheckinCount(Integer checkinCount) { this.checkinCount = checkinCount; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
}

package com.jiangyou.dto;
public class AddressRequest {
    private String consignee;
    private String phone;
    private String region;
    private String detail;
    private Boolean isDefault;
    public String getConsignee() { return consignee; } public void setConsignee(String consignee) { this.consignee = consignee; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getRegion() { return region; } public void setRegion(String region) { this.region = region; }
    public String getDetail() { return detail; } public void setDetail(String detail) { this.detail = detail; }
    public Boolean getIsDefault() { return isDefault; } public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
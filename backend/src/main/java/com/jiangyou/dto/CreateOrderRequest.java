package com.jiangyou.dto;
import java.util.List;
public class CreateOrderRequest {
    private Long addressId;
    private String remark;
    private List<CartItem> items;
    public Long getAddressId() { return addressId; } public void setAddressId(Long addressId) { this.addressId = addressId; }
    public String getRemark() { return remark; } public void setRemark(String remark) { this.remark = remark; }
    public List<CartItem> getItems() { return items; } public void setItems(List<CartItem> items) { this.items = items; }
    public static class CartItem {
        private Long productId;
        private Integer quantity;
        private String spec;
        public Long getProductId() { return productId; } public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getSpec() { return spec; } public void setSpec(String spec) { this.spec = spec; }
    }
}
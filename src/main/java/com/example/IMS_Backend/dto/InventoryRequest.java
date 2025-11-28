package com.example.IMS_Backend.dto;

public class InventoryRequest {
    private Long storeId;
    private Long productId;
    private Integer quantity;
    private Double costPrice;

    // Constructors
    public InventoryRequest() {}

    public InventoryRequest(Long storeId, Long productId, Integer quantity, Double costPrice) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.costPrice = costPrice;
    }

    // Getters and Setters
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
}
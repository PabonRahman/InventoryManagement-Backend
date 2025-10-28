package com.example.IMS_Backend.dto;

public class SaleDTO {
    private Long productId;
    private Long storeId;
    private Integer quantity;
    private Double price;
    private String saleDate; // format: "yyyy-MM-dd"
    private String description;

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getSaleDate() { return saleDate; }
    public void setSaleDate(String saleDate) { this.saleDate = saleDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

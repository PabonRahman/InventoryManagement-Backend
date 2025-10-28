package com.example.IMS_Backend.dto;

public class SaleResponseDTO {
    private Long id;
    private String productName;
    private String storeName;
    private Integer quantity;
    private Double price;
    private String saleDate;
    private String description;

    public SaleResponseDTO(Long id, String productName, String storeName, Integer quantity, Double price, String saleDate, String description) {
        this.id = id;
        this.productName = productName;
        this.storeName = storeName;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
        this.description = description;
    }

    // Getters
    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public String getStoreName() { return storeName; }
    public Integer getQuantity() { return quantity; }
    public Double getPrice() { return price; }
    public String getSaleDate() { return saleDate; }
    public String getDescription() { return description; }
}

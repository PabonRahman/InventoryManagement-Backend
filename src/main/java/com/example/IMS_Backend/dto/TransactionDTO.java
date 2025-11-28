package com.example.IMS_Backend.dto;

import com.example.IMS_Backend.model.TransactionType;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long storeId;
    private String storeName;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalAmount;
    private TransactionType type;
    private LocalDateTime transactionDate;
    private String description;

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(Long id, Long storeId, String storeName, Long productId, String productName,
                          Integer quantity, Double price, TransactionType type, LocalDateTime transactionDate,
                          String description) {
        this.id = id;
        this.storeId = storeId;
        this.storeName = storeName;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = quantity * price;
        this.type = type;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.price != null) {
            this.totalAmount = quantity * price;
        }
    }

    public Double getPrice() { return price; }
    public void setPrice(Double price) {
        this.price = price;
        if (this.quantity != null) {
            this.totalAmount = quantity * price;
        }
    }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
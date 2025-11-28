package com.example.IMS_Backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryResponseDTO {
    private Long id;
    private Long storeId;
    private String storeName;
    private String storeAddress;
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer quantity;
    private Double costPrice;

    // Constructor from Inventory entity
    public InventoryResponseDTO(com.example.IMS_Backend.model.Inventory inventory) {
        this.id = inventory.getId();
        this.storeId = inventory.getStore() != null ? inventory.getStore().getId() : null;
        this.storeName = inventory.getStore() != null ? inventory.getStore().getName() : null;
        this.storeAddress = inventory.getStore() != null ? inventory.getStore().getAddress() : null;
        this.productId = inventory.getProduct() != null ? inventory.getProduct().getId() : null;
        this.productName = inventory.getProduct() != null ? inventory.getProduct().getName() : null;
        this.productDescription = inventory.getProduct() != null ? inventory.getProduct().getDescription() : null;

        this.quantity = inventory.getQuantity();
        this.costPrice = inventory.getCostPrice();
    }
}
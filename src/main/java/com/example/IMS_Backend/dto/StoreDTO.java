package com.example.IMS_Backend.dto;

public class StoreDTO {
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private Boolean isActive;
    private Long productCount;
    private Long purchaseCount;
    private Long saleCount;

    // Constructors
    public StoreDTO() {}

    // Constructor from Store entity (without counts)
    public StoreDTO(com.example.IMS_Backend.model.Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.address = store.getAddress();
        this.contactNumber = store.getContactNumber();
        this.isActive = store.getIsActive();
        this.productCount = 0L;
        this.purchaseCount = 0L;
        this.saleCount = 0L;
    }

    // Constructor for repository query with all counts
    public StoreDTO(Long id, String name, String address, String contactNumber, Boolean isActive,
                    Long productCount, Long purchaseCount, Long saleCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.isActive = isActive;
        this.productCount = productCount;
        this.purchaseCount = purchaseCount;
        this.saleCount = saleCount;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getProductCount() { return productCount; }
    public void setProductCount(Long productCount) { this.productCount = productCount; }

    public Long getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Long purchaseCount) { this.purchaseCount = purchaseCount; }

    public Long getSaleCount() { return saleCount; }
    public void setSaleCount(Long saleCount) { this.saleCount = saleCount; }
}
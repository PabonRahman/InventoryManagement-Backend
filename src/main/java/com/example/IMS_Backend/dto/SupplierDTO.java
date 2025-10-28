package com.example.IMS_Backend.dto;

public class SupplierDTO {
    private Long id;
    private String name;
    private String contactEmail;
    private String phone;
    private String address;
    private Boolean isActive;      // Add this field
    private Long productCount;     // Add this field

    // Constructors
    public SupplierDTO() {}

    // Existing constructor (keep for backward compatibility)
    public SupplierDTO(Long id, String name, String contactEmail, String phone, String address) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.address = address;
        this.isActive = true;      // Default value
        this.productCount = 0L;    // Default value
    }

    // ✅ NEW: Constructor with all fields including product count
    public SupplierDTO(Long id, String name, String contactEmail, String phone, String address, Boolean isActive, Long productCount) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.address = address;
        this.isActive = isActive;
        this.productCount = productCount;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // ✅ ADD THESE NEW GETTERS/SETTERS
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getProductCount() { return productCount; }
    public void setProductCount(Long productCount) { this.productCount = productCount; }
}
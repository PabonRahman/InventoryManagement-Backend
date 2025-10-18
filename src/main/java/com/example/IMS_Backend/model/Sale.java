package com.example.IMS_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price; // price per unit

    @Temporal(TemporalType.DATE)
    @Column(name = "sale_date", nullable = false)
    private Date saleDate;

    private String description;

    public Sale() {}

    public Sale(Product product, Integer quantity, Double price, Date saleDate, String description) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
        this.description = description;
    }
}

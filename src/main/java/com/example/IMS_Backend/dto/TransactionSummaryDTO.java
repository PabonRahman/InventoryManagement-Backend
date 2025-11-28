package com.example.IMS_Backend.dto;

public class TransactionSummaryDTO {
    private Double totalPurchases;
    private Double totalSales;
    private Double netProfit;
    private Integer totalTransactions;
    private Integer purchaseCount;
    private Integer saleCount;

    public TransactionSummaryDTO() {}

    public TransactionSummaryDTO(Double totalPurchases, Double totalSales, Double netProfit,
                                 Integer totalTransactions, Integer purchaseCount, Integer saleCount) {
        this.totalPurchases = totalPurchases;
        this.totalSales = totalSales;
        this.netProfit = netProfit;
        this.totalTransactions = totalTransactions;
        this.purchaseCount = purchaseCount;
        this.saleCount = saleCount;
    }

    // Getters and Setters
    public Double getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(Double totalPurchases) { this.totalPurchases = totalPurchases; }

    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }

    public Double getNetProfit() { return netProfit; }
    public void setNetProfit(Double netProfit) { this.netProfit = netProfit; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }

    public Integer getSaleCount() { return saleCount; }
    public void setSaleCount(Integer saleCount) { this.saleCount = saleCount; }
}
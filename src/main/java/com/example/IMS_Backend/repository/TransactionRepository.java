package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.model.Transaction;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find transactions by store
    List<Transaction> findByStore(Store store);

    // Find transactions by store ID
    List<Transaction> findByStoreId(Long storeId);

    // Find transactions by product ID
    List<Transaction> findByProductId(Long productId);

    // Find transactions by type
    List<Transaction> findByType(TransactionType type);

    // Find transactions by type and store
    List<Transaction> findByTypeAndStoreId(TransactionType type, Long storeId);

    // Find transactions by date range
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find transactions by date range and store
    List<Transaction> findByTransactionDateBetweenAndStoreId(LocalDateTime startDate, LocalDateTime endDate, Long storeId);

    // Find transactions by date range and type
    List<Transaction> findByTransactionDateBetweenAndType(LocalDateTime startDate, LocalDateTime endDate, TransactionType type);

    // Get all transactions ordered by date (newest first)
    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
    List<Transaction> findAllByOrderByTransactionDateDesc();

    // Get transactions by store ordered by date (newest first)
    @Query("SELECT t FROM Transaction t WHERE t.store.id = :storeId ORDER BY t.transactionDate DESC")
    List<Transaction> findByStoreIdOrderByTransactionDateDesc(@Param("storeId") Long storeId);

    // Get transactions by product ordered by date (newest first)
    @Query("SELECT t FROM Transaction t WHERE t.product.id = :productId ORDER BY t.transactionDate DESC")
    List<Transaction> findByProductIdOrderByTransactionDateDesc(@Param("productId") Long productId);

    // Get transactions by type ordered by date (newest first)
    @Query("SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByTypeOrderByTransactionDateDesc(@Param("type") TransactionType type);

    // Get total purchase amount for a date range
    @Query("SELECT COALESCE(SUM(t.quantity * t.price), 0) FROM Transaction t WHERE t.type = 'PURCHASE' AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalPurchaseAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get total sale amount for a date range
    @Query("SELECT COALESCE(SUM(t.quantity * t.price), 0) FROM Transaction t WHERE t.type = 'SALE' AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalSaleAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get total purchase amount for a store and date range
    @Query("SELECT COALESCE(SUM(t.quantity * t.price), 0) FROM Transaction t WHERE t.type = 'PURCHASE' AND t.store.id = :storeId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalPurchaseAmountByStore(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get total sale amount for a store and date range
    @Query("SELECT COALESCE(SUM(t.quantity * t.price), 0) FROM Transaction t WHERE t.type = 'SALE' AND t.store.id = :storeId AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalSaleAmountByStore(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get recent transactions (last N days)
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate >= :startDate ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(@Param("startDate") LocalDateTime startDate);

    // Get transaction count by type
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = :type")
    Long countByType(@Param("type") TransactionType type);

    // Get total quantity sold for a product
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transaction t WHERE t.product.id = :productId AND t.type = 'SALE'")
    Integer getTotalQuantitySold(@Param("productId") Long productId);

    // Get total quantity purchased for a product
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transaction t WHERE t.product.id = :productId AND t.type = 'PURCHASE'")
    Integer getTotalQuantityPurchased(@Param("productId") Long productId);
}
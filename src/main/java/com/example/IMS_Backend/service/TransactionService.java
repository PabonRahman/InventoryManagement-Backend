package com.example.IMS_Backend.service;

import com.example.IMS_Backend.config.ResourceNotFoundException;
import com.example.IMS_Backend.dto.TransactionDTO;
import com.example.IMS_Backend.dto.TransactionSummaryDTO;
import com.example.IMS_Backend.model.*;
import com.example.IMS_Backend.repository.InventoryRepository;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import com.example.IMS_Backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all transactions as DTOs (newest first)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all transactions as Entities
    public List<Transaction> getAllTransactionEntities() {
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    // Get transaction by ID as DTO
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return convertToDTO(transaction);
    }

    // Get transaction by ID as Entity
    public Transaction getTransactionEntityById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    // Create a new transaction (purchase or sale) and update inventory
    @Transactional
    public TransactionDTO createTransaction(Long storeId, Long productId, Integer quantity,
                                            Double price, TransactionType type, String description) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with id: " + storeId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Get or create inventory
        Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                .orElse(new Inventory());

        // Set inventory properties if it's new
        if (inventory.getId() == null) {
            inventory.setStore(store);
            inventory.setProduct(product);
            inventory.setQuantity(0);
            inventory.setCostPrice(0.0);
        }

        // Update inventory based on transaction type
        if (type == TransactionType.SALE) {
            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for sale. Available: " + inventory.getQuantity() + ", Requested: " + quantity);
            }
            inventory.setQuantity(inventory.getQuantity() - quantity);
        } else if (type == TransactionType.PURCHASE) {
            inventory.setQuantity(inventory.getQuantity() + quantity);
            // Update cost price for purchases (simple average)
            if (inventory.getCostPrice() == null || inventory.getCostPrice() == 0) {
                inventory.setCostPrice(price);
            } else {
                // Weighted average cost calculation
                double totalValue = (inventory.getQuantity() * inventory.getCostPrice()) + (quantity * price);
                int totalQuantity = inventory.getQuantity() + quantity;
                inventory.setCostPrice(totalValue / totalQuantity);
            }
        }

        // Save inventory
        inventoryRepository.save(inventory);

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setStore(store);
        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    // Delete transaction and reverse inventory changes
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionEntityById(id);

        // Reverse inventory changes
        Inventory inventory = inventoryRepository.findByStoreAndProduct(transaction.getStore(), transaction.getProduct())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (transaction.getType() == TransactionType.SALE) {
            // Reverse sale: add back to inventory
            inventory.setQuantity(inventory.getQuantity() + transaction.getQuantity());
        } else if (transaction.getType() == TransactionType.PURCHASE) {
            // Reverse purchase: remove from inventory
            if (inventory.getQuantity() < transaction.getQuantity()) {
                throw new RuntimeException("Cannot reverse purchase: insufficient inventory");
            }
            inventory.setQuantity(inventory.getQuantity() - transaction.getQuantity());
        }

        inventoryRepository.save(inventory);
        transactionRepository.delete(transaction);
    }

    // Get transactions by type
    public List<TransactionDTO> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByTypeOrderByTransactionDateDesc(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transactions by store
    public List<TransactionDTO> getTransactionsByStore(Long storeId) {
        return transactionRepository.findByStoreIdOrderByTransactionDateDesc(storeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transactions by product
    public List<TransactionDTO> getTransactionsByProduct(Long productId) {
        return transactionRepository.findByProductIdOrderByTransactionDateDesc(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get recent transactions (last 30 days)
    public List<TransactionDTO> getRecentTransactions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return transactionRepository.findRecentTransactions(thirtyDaysAgo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transactions by date range
    public List<TransactionDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transaction summary
    public TransactionSummaryDTO getTransactionSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Double totalPurchases = transactionRepository.getTotalPurchaseAmount(startDate, endDate);
        Double totalSales = transactionRepository.getTotalSaleAmount(startDate, endDate);
        Long purchaseCount = transactionRepository.countByType(TransactionType.PURCHASE);
        Long saleCount = transactionRepository.countByType(TransactionType.SALE);
        Long totalTransactions = purchaseCount + saleCount;

        return new TransactionSummaryDTO(
                totalPurchases,
                totalSales,
                totalSales - totalPurchases,
                totalTransactions.intValue(),
                purchaseCount.intValue(),
                saleCount.intValue()
        );
    }

    // Get store transaction summary
    public TransactionSummaryDTO getTransactionSummaryByStore(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        Double totalPurchases = transactionRepository.getTotalPurchaseAmountByStore(storeId, startDate, endDate);
        Double totalSales = transactionRepository.getTotalSaleAmountByStore(storeId, startDate, endDate);
        Long purchaseCount = (long) transactionRepository.findByTypeAndStoreId(TransactionType.PURCHASE, storeId).size();
        Long saleCount = (long) transactionRepository.findByTypeAndStoreId(TransactionType.SALE, storeId).size();
        Long totalTransactions = purchaseCount + saleCount;

        return new TransactionSummaryDTO(
                totalPurchases,
                totalSales,
                totalSales - totalPurchases,
                totalTransactions.intValue(),
                purchaseCount.intValue(),
                saleCount.intValue()
        );
    }

    // Convert Transaction entity to DTO
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setStoreId(transaction.getStore().getId());
        dto.setStoreName(transaction.getStore().getName());
        dto.setProductId(transaction.getProduct().getId());
        dto.setProductName(transaction.getProduct().getName());
        dto.setQuantity(transaction.getQuantity());
        dto.setPrice(transaction.getPrice());
        dto.setTotalAmount(transaction.getQuantity() * transaction.getPrice());
        dto.setType(transaction.getType());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());

        return dto;
    }
}
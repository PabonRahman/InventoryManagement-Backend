package com.example.IMS_Backend.service;

import com.example.IMS_Backend.model.*;
import com.example.IMS_Backend.repository.InventoryRepository;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import com.example.IMS_Backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id " + id));
    }

    // Create a new transaction (purchase or sale) and update inventory
    public Transaction createTransaction(Long storeId, Long productId, Integer quantity,
                                         Double price, TransactionType type, String description) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for this product in store"));

        if (type == TransactionType.SALE) {
            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for sale");
            }
            inventory.setQuantity(inventory.getQuantity() - quantity);
        } else if (type == TransactionType.PURCHASE) {
            inventory.setQuantity(inventory.getQuantity() + quantity);
        }

        inventoryRepository.save(inventory);

        Transaction transaction = new Transaction();
        transaction.setStore(store);
        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setType(type);
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }

    // Delete transaction (optional: reverse inventory change)
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        // Optional: reverse inventory
        transactionRepository.delete(transaction);
    }
}

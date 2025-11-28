package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.TransactionDTO;
import com.example.IMS_Backend.dto.TransactionSummaryDTO;
import com.example.IMS_Backend.model.Transaction;
import com.example.IMS_Backend.model.TransactionType;
import com.example.IMS_Backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Get all transactions as DTOs
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all transactions as Entities (for compatibility)
    @GetMapping("/entities")
    public ResponseEntity<List<Transaction>> getAllTransactionEntities() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactionEntities();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        try {
            TransactionDTO transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a transaction (purchase or sale)
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestParam Long storeId,
                                               @RequestParam Long productId,
                                               @RequestParam Integer quantity,
                                               @RequestParam Double price,
                                               @RequestParam TransactionType type,
                                               @RequestParam(required = false) String description) {
        try {
            TransactionDTO transaction = transactionService.createTransaction(storeId, productId, quantity, price, type, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get transactions by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable TransactionType type) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByType(type);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get transactions by store
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStore(@PathVariable Long storeId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByStore(storeId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get transactions by product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByProduct(@PathVariable Long productId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByProduct(productId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get recent transactions (last 30 days)
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionDTO>> getRecentTransactions() {
        try {
            List<TransactionDTO> transactions = transactionService.getRecentTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get transactions by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get transaction summary
    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryDTO> getTransactionSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            TransactionSummaryDTO summary = transactionService.getTransactionSummary(startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get store transaction summary
    @GetMapping("/summary/store/{storeId}")
    public ResponseEntity<TransactionSummaryDTO> getTransactionSummaryByStore(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            TransactionSummaryDTO summary = transactionService.getTransactionSummaryByStore(storeId, startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.model.Transaction;
import com.example.IMS_Backend.model.TransactionType;
import com.example.IMS_Backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Get all transactions
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    // Create a transaction (purchase or sale)
    @PostMapping
    public Transaction createTransaction(@RequestParam Long storeId,
                                         @RequestParam Long productId,
                                         @RequestParam Integer quantity,
                                         @RequestParam Double price,
                                         @RequestParam TransactionType type,
                                         @RequestParam(required = false) String description) {
        return transactionService.createTransaction(storeId, productId, quantity, price, type, description);
    }

    // Delete a transaction
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}

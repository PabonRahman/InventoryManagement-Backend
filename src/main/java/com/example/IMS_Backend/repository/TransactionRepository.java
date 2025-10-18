package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.model.Transaction;
import com.example.IMS_Backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStore(Store store);
}

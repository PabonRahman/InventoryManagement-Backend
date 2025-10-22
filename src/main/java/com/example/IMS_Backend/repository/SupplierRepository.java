package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
    Optional<Supplier> findByName(String name);
    Optional<Supplier> findByPhone(String phone);
}
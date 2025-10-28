package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.dto.SupplierDTO;
import com.example.IMS_Backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Keep your existing methods
    List<Supplier> findByIsActiveTrue();
    Optional<Supplier> findByName(String name);
    boolean existsByNameAndIsActiveTrue(String name);

    // ✅ ADD THIS METHOD TO GET SUPPLIERS WITH PRODUCT COUNTS
    @Query("SELECT new com.example.IMS_Backend.dto.SupplierDTO(s.id, s.name, s.contactEmail, s.phone, s.address, s.isActive, COUNT(p.id)) " +
            "FROM Supplier s LEFT JOIN s.products p " +
            "WHERE s.isActive = true " +
            "GROUP BY s.id, s.name, s.contactEmail, s.phone, s.address, s.isActive " +
            "ORDER BY s.name")
    List<SupplierDTO> findAllActiveWithProductCount();

    // ✅ Optional: Get supplier by ID with product count
    @Query("SELECT new com.example.IMS_Backend.dto.SupplierDTO(s.id, s.name, s.contactEmail, s.phone, s.address, s.isActive, COUNT(p.id)) " +
            "FROM Supplier s LEFT JOIN s.products p " +
            "WHERE s.id = :supplierId AND s.isActive = true " +
            "GROUP BY s.id, s.name, s.contactEmail, s.phone, s.address, s.isActive")
    Optional<SupplierDTO> findByIdWithProductCount(Long supplierId);
}
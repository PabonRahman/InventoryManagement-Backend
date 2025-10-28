package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.dto.StoreDTO;
import com.example.IMS_Backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);

    // Get all stores with product, purchase, and sale counts
    @Query("SELECT new com.example.IMS_Backend.dto.StoreDTO(" +
            "s.id, s.name, s.address, s.contactNumber, s.isActive, " +
            "COUNT(DISTINCT p.id), COUNT(DISTINCT pur.id), COUNT(DISTINCT sa.id)) " +
            "FROM Store s " +
            "LEFT JOIN s.products p " +
            "LEFT JOIN s.purchases pur " +
            "LEFT JOIN s.sales sa " +
            "GROUP BY s.id, s.name, s.address, s.contactNumber, s.isActive")
    List<StoreDTO> findAllStoresWithCounts();

    // Get single store with counts by ID
    @Query("SELECT new com.example.IMS_Backend.dto.StoreDTO(" +
            "s.id, s.name, s.address, s.contactNumber, s.isActive, " +
            "COUNT(DISTINCT p.id), COUNT(DISTINCT pur.id), COUNT(DISTINCT sa.id)) " +
            "FROM Store s " +
            "LEFT JOIN s.products p " +
            "LEFT JOIN s.purchases pur " +
            "LEFT JOIN s.sales sa " +
            "WHERE s.id = :storeId " +
            "GROUP BY s.id, s.name, s.address, s.contactNumber, s.isActive")
    Optional<StoreDTO> findStoreWithCountsById(@Param("storeId") Long storeId);

    // Search stores with counts
    @Query("SELECT new com.example.IMS_Backend.dto.StoreDTO(" +
            "s.id, s.name, s.address, s.contactNumber, s.isActive, " +
            "COUNT(DISTINCT p.id), COUNT(DISTINCT pur.id), COUNT(DISTINCT sa.id)) " +
            "FROM Store s " +
            "LEFT JOIN s.products p " +
            "LEFT JOIN s.purchases pur " +
            "LEFT JOIN s.sales sa " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR s.contactNumber LIKE CONCAT('%', :search, '%') " +
            "GROUP BY s.id, s.name, s.address, s.contactNumber, s.isActive")
    List<StoreDTO> searchStoresWithCounts(@Param("search") String search);
}
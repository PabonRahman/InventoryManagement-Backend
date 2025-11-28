package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.model.Inventory;
import com.example.IMS_Backend.model.Product;
import com.example.IMS_Backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory record for a specific product in a store
    Optional<Inventory> findByStoreAndProduct(Store store, Product product);

    // Get all inventory records for a specific store
    List<Inventory> findByStore(Store store);

    // Find by storeId and productId - ADD THIS METHOD
    @Query("SELECT i FROM Inventory i WHERE i.store.id = :storeId AND i.product.id = :productId")
    Optional<Inventory> findByStoreIdAndProductId(@Param("storeId") Long storeId, @Param("productId") Long productId);

    // Get all inventories for a specific store ID
    @Query("SELECT i FROM Inventory i WHERE i.store.id = :storeId")
    List<Inventory> findByStoreId(@Param("storeId") Long storeId);
}
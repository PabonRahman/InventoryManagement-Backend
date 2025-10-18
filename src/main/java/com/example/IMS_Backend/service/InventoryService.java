package com.example.IMS_Backend.service;

import com.example.IMS_Backend.model.Inventory;
import com.example.IMS_Backend.model.Product;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.repository.InventoryRepository;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all inventory records
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // Get inventory by ID
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id " + id));
    }

    // Get inventory for a store and product
    public Inventory getInventoryByStoreAndProduct(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return inventoryRepository.findByStoreAndProduct(store, product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for this product in store"));
    }

    // Create or update inventory
    public Inventory createOrUpdateInventory(Long storeId, Long productId, Integer quantity, Double costPrice) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                .orElse(new Inventory());
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventory.setCostPrice(costPrice);

        return inventoryRepository.save(inventory);
    }

    // Delete inventory by ID
    public void deleteInventory(Long id) {
        Inventory inventory = getInventoryById(id);
        inventoryRepository.delete(inventory);
    }
}

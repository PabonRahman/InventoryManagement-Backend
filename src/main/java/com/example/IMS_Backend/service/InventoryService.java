package com.example.IMS_Backend.service;

import com.example.IMS_Backend.config.ResourceNotFoundException;
import com.example.IMS_Backend.dto.InventoryResponseDTO;
import com.example.IMS_Backend.model.Inventory;
import com.example.IMS_Backend.model.Product;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.repository.InventoryRepository;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all inventory records as DTOs
    public List<InventoryResponseDTO> getAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(InventoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Get inventory by ID as Entity (for internal use)
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    // Get inventory by ID as DTO
    public InventoryResponseDTO getInventoryDTOById(Long id) {
        Inventory inventory = getInventoryById(id);
        return new InventoryResponseDTO(inventory);
    }

    // Get inventory by store and product
    public Inventory getInventoryByStoreAndProduct(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return inventoryRepository.findByStoreAndProduct(store, product)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for store " + storeId + " and product " + productId));
    }

    // Create new inventory
    public Inventory createInventory(Long storeId, Long productId, Integer quantity, Double costPrice) {
        // Validate inputs
        if (storeId == null) {
            throw new RuntimeException("Store ID is required");
        }
        if (productId == null) {
            throw new RuntimeException("Product ID is required");
        }
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Quantity must be a non-negative number");
        }
        if (costPrice == null || costPrice < 0) {
            throw new RuntimeException("Cost price must be a non-negative number");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if inventory already exists
        Optional<Inventory> existingInventory = inventoryRepository.findByStoreIdAndProductId(storeId, productId);
        if (existingInventory.isPresent()) {
            throw new RuntimeException("Inventory already exists for store " + storeId + " and product " + productId);
        }

        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventory.setCostPrice(costPrice);

        return inventoryRepository.save(inventory);
    }

    // Update existing inventory
    public Inventory updateInventory(Long inventoryId, Integer quantity, Double costPrice) {
        // Validate inputs
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Quantity must be a non-negative number");
        }
        if (costPrice == null || costPrice < 0) {
            throw new RuntimeException("Cost price must be a non-negative number");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + inventoryId));

        inventory.setQuantity(quantity);
        inventory.setCostPrice(costPrice);

        return inventoryRepository.save(inventory);
    }

    // Create or update inventory (for backward compatibility)
    public Inventory createOrUpdateInventory(Long storeId, Long productId, Integer quantity, Double costPrice) {
        Optional<Inventory> existingInventory = inventoryRepository.findByStoreIdAndProductId(storeId, productId);

        if (existingInventory.isPresent()) {
            // Update existing
            Inventory inventory = existingInventory.get();
            inventory.setQuantity(quantity);
            inventory.setCostPrice(costPrice);
            return inventoryRepository.save(inventory);
        } else {
            // Create new
            return createInventory(storeId, productId, quantity, costPrice);
        }
    }

    // Delete inventory by ID
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        inventoryRepository.delete(inventory);
    }

    // Get inventories by store ID as DTOs
    public List<InventoryResponseDTO> getInventoriesByStoreId(Long storeId) {
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);
        return inventories.stream()
                .map(InventoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    // AUTO-UPDATE METHODS FOR PURCHASES AND SALES

    /**
     * Automatically update inventory when a purchase is made
     */
    @Transactional
    public Inventory updateInventoryAfterPurchase(Long storeId, Long productId, Integer purchaseQuantity, Double unitCost) {
        try {
            Optional<Inventory> existingInventory = inventoryRepository.findByStoreIdAndProductId(storeId, productId);

            if (existingInventory.isPresent()) {
                // Update existing inventory
                Inventory inventory = existingInventory.get();
                int newQuantity = inventory.getQuantity() + purchaseQuantity;

                // Update cost price (weighted average)
                Double newCostPrice = calculateWeightedAverageCost(
                        inventory.getQuantity(), inventory.getCostPrice(),
                        purchaseQuantity, unitCost
                );

                inventory.setQuantity(newQuantity);
                inventory.setCostPrice(newCostPrice);
                return inventoryRepository.save(inventory);
            } else {
                // Create new inventory record
                return createInventory(storeId, productId, purchaseQuantity, unitCost);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating inventory after purchase: " + e.getMessage());
        }
    }

    /**
     * Automatically update inventory when a sale is made
     */
    @Transactional
    public Inventory updateInventoryAfterSale(Long storeId, Long productId, Integer saleQuantity) {
        try {
            Inventory inventory = getInventoryByStoreAndProduct(storeId, productId);

            if (inventory.getQuantity() < saleQuantity) {
                throw new RuntimeException("Insufficient inventory. Available: " + inventory.getQuantity() + ", Requested: " + saleQuantity);
            }

            int newQuantity = inventory.getQuantity() - saleQuantity;
            inventory.setQuantity(newQuantity);

            return inventoryRepository.save(inventory);
        } catch (Exception e) {
            throw new RuntimeException("Error updating inventory after sale: " + e.getMessage());
        }
    }

    /**
     * Update inventory after sale or return (decrease quantity)
     */
    @Transactional
    public Inventory updateInventoryAfterSaleOrReturn(Long storeId, Long productId, Integer quantity) {
        try {
            Inventory inventory = getInventoryByStoreAndProduct(storeId, productId);
            int newQuantity = inventory.getQuantity() - quantity;
            if (newQuantity < 0) newQuantity = 0;

            inventory.setQuantity(newQuantity);
            return inventoryRepository.save(inventory);
        } catch (Exception e) {
            // If inventory doesn't exist, it's fine for returns
            System.err.println("Inventory not found for update: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update inventory after return (increase quantity)
     */
    @Transactional
    public Inventory updateInventoryAfterReturn(Long storeId, Long productId, Integer quantity) {
        try {
            Inventory inventory = getInventoryByStoreAndProduct(storeId, productId);
            int newQuantity = inventory.getQuantity() + quantity;
            inventory.setQuantity(newQuantity);
            return inventoryRepository.save(inventory);
        } catch (Exception e) {
            // Create new inventory if it doesn't exist
            return createInventory(storeId, productId, quantity, 0.0);
        }
    }

    /**
     * Calculate weighted average cost for inventory valuation
     */
    private Double calculateWeightedAverageCost(Integer existingQty, Double existingCost,
                                                Integer newQty, Double newCost) {
        if (existingQty == 0) return newCost;

        double totalValue = (existingQty * existingCost) + (newQty * newCost);
        int totalQuantity = existingQty + newQty;

        return totalValue / totalQuantity;
    }

    /**
     * Get current stock level for a product in a store
     */
    public Integer getCurrentStockLevel(Long storeId, Long productId) {
        try {
            Inventory inventory = getInventoryByStoreAndProduct(storeId, productId);
            return inventory.getQuantity();
        } catch (ResourceNotFoundException e) {
            return 0; // No inventory record means zero stock
        }
    }

    /**
     * Check if sufficient inventory exists for a sale
     */
    public boolean hasSufficientInventory(Long storeId, Long productId, Integer requestedQuantity) {
        try {
            Integer currentStock = getCurrentStockLevel(storeId, productId);
            return currentStock >= requestedQuantity;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get low stock items (quantity less than threshold)
     */
    public List<InventoryResponseDTO> getLowStockItems(Integer threshold) {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .filter(inv -> inv.getQuantity() <= threshold)
                .map(InventoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get out of stock items
     */
    public List<InventoryResponseDTO> getOutOfStockItems() {
        return getLowStockItems(0);
    }

    /**
     * Get total inventory value across all stores
     */
    public Double getTotalInventoryValue() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .mapToDouble(inv -> inv.getQuantity() * inv.getCostPrice())
                .sum();
    }

    /**
     * Get inventory value for a specific store
     */
    public Double getInventoryValueByStore(Long storeId) {
        List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);
        return inventories.stream()
                .mapToDouble(inv -> inv.getQuantity() * inv.getCostPrice())
                .sum();
    }
}
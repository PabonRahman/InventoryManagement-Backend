package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.model.Inventory;
import com.example.IMS_Backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Get all inventories
    @GetMapping
    public List<Inventory> getAllInventories() {
        return inventoryService.getAllInventories();
    }

    // Get inventory by ID
    @GetMapping("/{id}")
    public Inventory getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // Get inventory by store and product
    @GetMapping("/store/{storeId}/product/{productId}")
    public Inventory getInventoryByStoreAndProduct(@PathVariable Long storeId,
                                                   @PathVariable Long productId) {
        return inventoryService.getInventoryByStoreAndProduct(storeId, productId);
    }

    // Create or update inventory
    @PostMapping
    public Inventory createOrUpdateInventory(@RequestParam Long storeId,
                                             @RequestParam Long productId,
                                             @RequestParam Integer quantity,
                                             @RequestParam Double costPrice) {
        return inventoryService.createOrUpdateInventory(storeId, productId, quantity, costPrice);
    }

    // Delete inventory
    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}

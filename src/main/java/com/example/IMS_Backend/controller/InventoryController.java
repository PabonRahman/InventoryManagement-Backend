package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.InventoryRequest;
import com.example.IMS_Backend.dto.InventoryResponseDTO;
import com.example.IMS_Backend.model.Inventory;
import com.example.IMS_Backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Get all inventories - FIXED: Returns DTOs instead of entities
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        try {
            List<InventoryResponseDTO> inventories = inventoryService.getAllInventories();
            return ResponseEntity.ok(inventories);
        } catch (Exception e) {
            System.err.println("Error getting inventories: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get inventory by ID
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        try {
            InventoryResponseDTO inventory = inventoryService.getInventoryDTOById(id);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get inventory by store and product
    @GetMapping(value = "/store/{storeId}/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Inventory> getInventoryByStoreAndProduct(@PathVariable Long storeId,
                                                                   @PathVariable Long productId) {
        try {
            Inventory inventory = inventoryService.getInventoryByStoreAndProduct(storeId, productId);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new inventory
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequest request) {
        try {
            System.out.println("Received inventory request - StoreId: " + request.getStoreId() +
                    ", ProductId: " + request.getProductId() +
                    ", Quantity: " + request.getQuantity() +
                    ", CostPrice: " + request.getCostPrice());

            Inventory inventory = inventoryService.createInventory(
                    request.getStoreId(),
                    request.getProductId(),
                    request.getQuantity(),
                    request.getCostPrice()
            );

            System.out.println("Successfully created inventory: " + inventory.getId());
            InventoryResponseDTO responseDTO = new InventoryResponseDTO(inventory);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            System.err.println("Error creating inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    // Update inventory
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateInventory(@PathVariable Long id,
                                             @RequestBody InventoryRequest request) {
        try {
            Inventory inventory = inventoryService.updateInventory(
                    id, request.getQuantity(), request.getCostPrice()
            );
            InventoryResponseDTO responseDTO = new InventoryResponseDTO(inventory);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    // Delete inventory
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok().body("{\"message\": \"Inventory deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    // Get inventories by store ID
    @GetMapping(value = "/store/{storeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InventoryResponseDTO>> getInventoriesByStoreId(@PathVariable Long storeId) {
        try {
            List<InventoryResponseDTO> inventories = inventoryService.getInventoriesByStoreId(storeId);
            return ResponseEntity.ok(inventories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
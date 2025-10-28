package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.StoreDTO;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // FIXED: Now using methods that provide ACTUAL COUNTS
    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStoresWithCounts(); // This will show real counts
    }

    // FIXED: Now using methods that provide ACTUAL COUNTS
    @GetMapping("/{id}")
    public StoreDTO getStoreById(@PathVariable Long id) {
        return storeService.getStoreWithCounts(id); // This will show real counts
    }

    @PostMapping
    public StoreDTO createStore(@RequestBody Store store) {
        // For create/update, we can use the old DTO methods since counts will be 0 for new stores
        Store createdStore = storeService.createStore(store);
        return storeService.convertToDTO(createdStore);
    }

    @PutMapping("/{id}")
    public StoreDTO updateStore(@PathVariable Long id, @RequestBody Store store) {
        Store updatedStore = storeService.updateStore(id, store);
        return storeService.convertToDTO(updatedStore);
    }

    @DeleteMapping("/{id}")
    public void deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
    }

    @DeleteMapping("/hard/{id}")
    public void hardDeleteStore(@PathVariable Long id) {
        storeService.hardDeleteStore(id);
    }

    // Optional: Add search endpoint
    @GetMapping("/search")
    public List<StoreDTO> searchStores(@RequestParam String query) {
        return storeService.searchStoresWithCounts(query);
    }
}
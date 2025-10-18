package com.example.IMS_Backend.service;

import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    // Get all stores
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // Get a store by ID
    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + id));
    }

    // Create a new store
    public Store createStore(Store store) {
        if(storeRepository.existsByName(store.getName())) {
            throw new RuntimeException("Store with this name already exists");
        }
        return storeRepository.save(store);
    }

    // Update an existing store
    public Store updateStore(Long id, Store storeDetails) {
        Store store = getStoreById(id);
        store.setName(storeDetails.getName());
        store.setAddress(storeDetails.getAddress());
        store.setContactNumber(storeDetails.getContactNumber());
        return storeRepository.save(store);
    }

    // Delete a store
    public void deleteStore(Long id) {
        Store store = getStoreById(id);
        storeRepository.delete(store);
    }
}

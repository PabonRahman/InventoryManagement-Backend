package com.example.IMS_Backend.service;

import com.example.IMS_Backend.dto.StoreDTO;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    // Entity CRUD
    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + id));
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store createStore(Store store) {
        if (storeRepository.existsByName(store.getName())) {
            throw new RuntimeException("Store with this name already exists");
        }
        store.setIsActive(true);
        return storeRepository.save(store);
    }

    public Store updateStore(Long id, Store storeDetails) {
        Store store = getStoreById(id);
        store.setName(storeDetails.getName());
        store.setAddress(storeDetails.getAddress());
        store.setContactNumber(storeDetails.getContactNumber());
        store.setIsActive(storeDetails.getIsActive() != null ? storeDetails.getIsActive() : store.getIsActive());
        return storeRepository.save(store);
    }

    public void deleteStore(Long id) {
        Store store = getStoreById(id);
        store.setIsActive(false); // soft delete
        storeRepository.save(store);
    }

    public void hardDeleteStore(Long id) {
        Store store = getStoreById(id);
        storeRepository.delete(store);
    }

    // --- DTO Methods with ACTUAL COUNTS ---

    // Get all stores with actual counts (using repository query)
    public List<StoreDTO> getAllStoresWithCounts() {
        return storeRepository.findAllStoresWithCounts();
    }

    // Get single store with actual counts (using repository query)
    public StoreDTO getStoreWithCounts(Long id) {
        return storeRepository.findStoreWithCountsById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id: " + id));
    }

    // Search stores with actual counts
    public List<StoreDTO> searchStoresWithCounts(String searchQuery) {
        return storeRepository.searchStoresWithCounts(searchQuery);
    }

    // OLD METHOD - Don't use this for your UI (it sets counts to 0)
    @Deprecated
    public StoreDTO convertToDTO(Store store) {
        return new StoreDTO(store);
    }

    // OLD METHOD - Don't use this for your UI
    @Deprecated
    public List<StoreDTO> getAllStoresDTO() {
        return storeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // OLD METHOD - Don't use this for your UI
    @Deprecated
    public StoreDTO getStoreDTOById(Long id) {
        return convertToDTO(getStoreById(id));
    }
}
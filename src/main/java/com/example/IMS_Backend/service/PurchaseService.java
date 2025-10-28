package com.example.IMS_Backend.service;

import com.example.IMS_Backend.dto.PurchaseDTO;
import com.example.IMS_Backend.dto.PurchaseRequest;
import com.example.IMS_Backend.model.Product;
import com.example.IMS_Backend.model.Purchase;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.model.Supplier;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.PurchaseRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import com.example.IMS_Backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private StoreRepository storeRepository;

    // Get all purchases
    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get purchase by ID
    public PurchaseDTO getPurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));
        return toDTO(purchase);
    }

    // Save new purchase
    public PurchaseDTO savePurchase(PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        Purchase purchase = new Purchase(
                product,
                supplier,
                store,
                request.getQuantity(),
                request.getPrice(),
                request.getPurchaseDate(),
                request.getDescription()
        );

        return toDTO(purchaseRepository.save(purchase));
    }

    // Update existing purchase
    public PurchaseDTO updatePurchase(Long id, PurchaseRequest request) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        existing.setProduct(product);
        existing.setSupplier(supplier);
        existing.setStore(store);
        existing.setQuantity(request.getQuantity());
        existing.setPrice(request.getPrice());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setDescription(request.getDescription());

        return toDTO(purchaseRepository.save(existing));
    }

    // Delete purchase
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));
        purchaseRepository.delete(purchase);
    }

    // Convert Purchase -> DTO
    private PurchaseDTO toDTO(Purchase purchase) {
        return new PurchaseDTO(
                purchase.getId(),
                purchase.getProduct().getId(),
                purchase.getProduct().getName(),
                purchase.getSupplier().getId(),
                purchase.getSupplier().getName(),
                purchase.getStore().getId(),
                purchase.getStore().getName(),
                purchase.getQuantity(),
                purchase.getPrice(),
                purchase.getPurchaseDate(),
                purchase.getDescription()
        );
    }
}

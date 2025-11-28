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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private InventoryService inventoryService; // Add this

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

    // Save new purchase - ENHANCED with inventory update
    public PurchaseDTO savePurchase(PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        try {
            // Update product quantity

            productRepository.save(product);

            // AUTO-UPDATE INVENTORY
            inventoryService.updateInventoryAfterPurchase(
                    request.getStoreId(),
                    request.getProductId(),
                    request.getQuantity(),
                    request.getPrice()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing purchase: " + e.getMessage());
        }

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

    // Update existing purchase - ENHANCED with inventory update
    public PurchaseDTO updatePurchase(Long id, PurchaseRequest request) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        // Calculate quantity difference for inventory adjustment
        int quantityDiff = request.getQuantity() - existing.getQuantity();

        // Update product quantity
        if (quantityDiff != 0) {

            productRepository.save(product);

            // AUTO-UPDATE INVENTORY based on quantity difference
            if (quantityDiff > 0) {
                // Additional quantity purchased
                inventoryService.updateInventoryAfterPurchase(
                        request.getStoreId(),
                        request.getProductId(),
                        quantityDiff,
                        request.getPrice()
                );
            } else {
                // Quantity reduced (like a return)
                inventoryService.updateInventoryAfterSaleOrReturn(
                        request.getStoreId(),
                        request.getProductId(),
                        Math.abs(quantityDiff)
                );
            }
        }

        existing.setProduct(product);
        existing.setSupplier(supplier);
        existing.setStore(store);
        existing.setQuantity(request.getQuantity());
        existing.setPrice(request.getPrice());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setDescription(request.getDescription());

        return toDTO(purchaseRepository.save(existing));
    }

    // Delete purchase - ENHANCED with inventory update
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        // Restore product quantity
        Product product = purchase.getProduct();

        productRepository.save(product);

        // AUTO-UPDATE INVENTORY (reverse the purchase)
        inventoryService.updateInventoryAfterSaleOrReturn(
                purchase.getStore().getId(),
                purchase.getProduct().getId(),
                purchase.getQuantity()
        );

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
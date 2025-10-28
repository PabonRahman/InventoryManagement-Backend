package com.example.IMS_Backend.service;

import com.example.IMS_Backend.dto.SupplierDTO;
import com.example.IMS_Backend.model.Supplier;
import com.example.IMS_Backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // ✅ OPTION 1: Get suppliers with product counts (using custom query)
    public List<SupplierDTO> getAllSuppliersWithCounts() {
        return supplierRepository.findAllActiveWithProductCount();
    }

    // ✅ OPTION 2: Get suppliers without counts (keep for backward compatibility)
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue()
                .stream()
                .map(s -> new SupplierDTO(s.getId(), s.getName(), s.getContactEmail(), s.getPhone(), s.getAddress()))
                .collect(Collectors.toList());
    }

    // ✅ GET SINGLE SUPPLIER WITH PRODUCT COUNT
    public SupplierDTO getSupplierWithCount(Long id) {
        return supplierRepository.findByIdWithProductCount(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));
    }

    // Fetch by ID (without count - keep for backward compatibility)
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));
        return new SupplierDTO(supplier.getId(), supplier.getName(), supplier.getContactEmail(), supplier.getPhone(), supplier.getAddress());
    }

    // Create supplier (reactivate if soft-deleted)
    public SupplierDTO createSupplier(Supplier supplier) {
        Optional<Supplier> existing = supplierRepository.findByName(supplier.getName());

        if (existing.isPresent()) {
            Supplier existingSupplier = existing.get();
            if (Boolean.TRUE.equals(existingSupplier.getIsActive())) {
                throw new RuntimeException("Supplier with name '" + supplier.getName() + "' already exists");
            } else {
                // Reactivate soft-deleted supplier
                existingSupplier.setIsActive(true);
                existingSupplier.setContactEmail(supplier.getContactEmail());
                existingSupplier.setPhone(supplier.getPhone());
                existingSupplier.setAddress(supplier.getAddress());
                Supplier saved = supplierRepository.save(existingSupplier);
                return new SupplierDTO(saved.getId(), saved.getName(), saved.getContactEmail(), saved.getPhone(), saved.getAddress());
            }
        }

        supplier.setIsActive(true);
        Supplier saved = supplierRepository.save(supplier);
        return new SupplierDTO(saved.getId(), saved.getName(), saved.getContactEmail(), saved.getPhone(), saved.getAddress());
    }

    // Update supplier
    public SupplierDTO updateSupplier(Long id, Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));

        supplier.setName(supplierDetails.getName());
        supplier.setContactEmail(supplierDetails.getContactEmail());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setAddress(supplierDetails.getAddress());

        Supplier updated = supplierRepository.save(supplier);
        return new SupplierDTO(updated.getId(), updated.getName(), updated.getContactEmail(), updated.getPhone(), updated.getAddress());
    }

    // Soft delete supplier
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id " + id));
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    // ✅ CHECK IF SUPPLIER HAS PRODUCTS (for validation before deletion)
    public boolean hasProducts(Long supplierId) {
        SupplierDTO supplier = getSupplierWithCount(supplierId);
        return supplier.getProductCount() != null && supplier.getProductCount() > 0;
    }

    // ✅ GET SUPPLIER PRODUCT COUNT
    public Long getProductCount(Long supplierId) {
        SupplierDTO supplier = getSupplierWithCount(supplierId);
        return supplier.getProductCount() != null ? supplier.getProductCount() : 0L;
    }
}
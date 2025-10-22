package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.model.Supplier;
import com.example.IMS_Backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        return supplier.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        try {
            // Use getPhone() instead of getContactNumber()
            if (supplier.getPhone() == null || supplier.getPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Phone number is required");
            }

            // Check if supplier with same name already exists
            if (supplierRepository.existsByName(supplier.getName())) {
                return ResponseEntity.badRequest().body("Supplier with name '" + supplier.getName() + "' already exists");
            }

            // Check if phone number already exists
            if (supplierRepository.existsByPhone(supplier.getPhone())) {
                return ResponseEntity.badRequest().body("Supplier with this phone number already exists");
            }

            Supplier savedSupplier = supplierRepository.save(supplier);
            return ResponseEntity.ok(savedSupplier);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating supplier: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        try {
            Optional<Supplier> existingSupplier = supplierRepository.findById(id);
            if (existingSupplier.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Supplier supplier = existingSupplier.get();

            // Check if name is being changed and if new name already exists
            if (!supplier.getName().equals(supplierDetails.getName()) &&
                    supplierRepository.existsByName(supplierDetails.getName())) {
                return ResponseEntity.badRequest().body("Supplier with name '" + supplierDetails.getName() + "' already exists");
            }

            // Check if phone is being changed and if new phone already exists
            if (!supplier.getPhone().equals(supplierDetails.getPhone()) &&
                    supplierRepository.existsByPhone(supplierDetails.getPhone())) {
                return ResponseEntity.badRequest().body("Supplier with phone number '" + supplierDetails.getPhone() + "' already exists");
            }

            supplier.setName(supplierDetails.getName());
            supplier.setContactEmail(supplierDetails.getContactEmail());
            supplier.setPhone(supplierDetails.getPhone()); // Use setPhone()
            supplier.setAddress(supplierDetails.getAddress());

            Supplier updatedSupplier = supplierRepository.save(supplier);
            return ResponseEntity.ok(updatedSupplier);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating supplier: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            if (!supplierRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            supplierRepository.deleteById(id);
            return ResponseEntity.ok().body("Supplier deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting supplier: " + e.getMessage());
        }
    }
}
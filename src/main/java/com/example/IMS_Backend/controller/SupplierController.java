package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.SupplierDTO;
import com.example.IMS_Backend.model.Supplier;
import com.example.IMS_Backend.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // ✅ MAIN ENDPOINT: Get all active suppliers WITH PRODUCT COUNTS
    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAllActiveWithProductCount();
    }

    // ✅ ALTERNATIVE: Get suppliers without counts (for backward compatibility)
    @GetMapping("/basic")
    public List<SupplierDTO> getSuppliersBasic() {
        return supplierRepository.findByIsActiveTrue()
                .stream()
                .map(s -> new SupplierDTO(s.getId(), s.getName(), s.getContactEmail(), s.getPhone(), s.getAddress()))
                .collect(Collectors.toList());
    }

    // ✅ Get supplier by ID WITH PRODUCT COUNT
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        Optional<SupplierDTO> supplier = supplierRepository.findByIdWithProductCount(id);
        return supplier.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get supplier by ID without product count (alternative)
    @GetMapping("/{id}/basic")
    public ResponseEntity<SupplierDTO> getSupplierByIdBasic(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        return supplier.map(s -> ResponseEntity.ok(
                        new SupplierDTO(s.getId(), s.getName(), s.getContactEmail(), s.getPhone(), s.getAddress())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get supplier product count only
    @GetMapping("/{id}/product-count")
    public ResponseEntity<Map<String, Long>> getSupplierProductCount(@PathVariable Long id) {
        Optional<SupplierDTO> supplier = supplierRepository.findByIdWithProductCount(id);
        if (supplier.isPresent()) {
            Long productCount = supplier.get().getProductCount() != null ? supplier.get().getProductCount() : 0L;
            return ResponseEntity.ok(Map.of("productCount", productCount));
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Create supplier (reactivates if soft deleted)
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody Supplier supplier) {
        Optional<Supplier> existing = supplierRepository.findByName(supplier.getName());

        if (existing.isPresent()) {
            Supplier existingSupplier = existing.get();
            if (Boolean.TRUE.equals(existingSupplier.getIsActive())) {
                return ResponseEntity.badRequest().build();
            } else {
                // Reactivate soft-deleted supplier
                existingSupplier.setIsActive(true);
                existingSupplier.setContactEmail(supplier.getContactEmail());
                existingSupplier.setPhone(supplier.getPhone());
                existingSupplier.setAddress(supplier.getAddress());
                Supplier saved = supplierRepository.save(existingSupplier);
                return ResponseEntity.ok(new SupplierDTO(saved.getId(), saved.getName(), saved.getContactEmail(),
                        saved.getPhone(), saved.getAddress()));
            }
        }

        supplier.setIsActive(true);
        Supplier saved = supplierRepository.save(supplier);
        return ResponseEntity.ok(new SupplierDTO(saved.getId(), saved.getName(), saved.getContactEmail(),
                saved.getPhone(), saved.getAddress()));
    }

    // ✅ Update supplier
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody Supplier updatedSupplier) {
        return supplierRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedSupplier.getName());
                    existing.setContactEmail(updatedSupplier.getContactEmail());
                    existing.setPhone(updatedSupplier.getPhone());
                    existing.setAddress(updatedSupplier.getAddress());
                    Supplier saved = supplierRepository.save(existing);
                    return ResponseEntity.ok(new SupplierDTO(saved.getId(), saved.getName(),
                            saved.getContactEmail(), saved.getPhone(), saved.getAddress()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Soft delete supplier (with product count check)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> softDeleteSupplier(@PathVariable Long id) {
        return supplierRepository.findById(id)
                .map(existing -> {
                    // ✅ Check if supplier has products before deletion
                    Optional<SupplierDTO> supplierWithCount = supplierRepository.findByIdWithProductCount(id);
                    if (supplierWithCount.isPresent()) {
                        Long productCount = supplierWithCount.get().getProductCount() != null ?
                                supplierWithCount.get().getProductCount() : 0L;
                        if (productCount > 0) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error",
                                            "Cannot delete supplier. There are " + productCount +
                                                    " products associated with this supplier. " +
                                                    "Please reassign or delete the products first."));
                        }
                    }

                    existing.setIsActive(false);
                    supplierRepository.save(existing);
                    return ResponseEntity.ok(Map.of("message", "Supplier deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Supplier not found")));
    }

    // ✅ Check if supplier can be deleted (has no products)
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Map<String, Object>> canDeleteSupplier(@PathVariable Long id) {
        Optional<SupplierDTO> supplier = supplierRepository.findByIdWithProductCount(id);
        if (supplier.isPresent()) {
            Long productCount = supplier.get().getProductCount() != null ? supplier.get().getProductCount() : 0L;
            boolean canDelete = productCount == 0;
            return ResponseEntity.ok(Map.of(
                    "canDelete", canDelete,
                    "productCount", productCount,
                    "message", canDelete ?
                            "Supplier can be deleted" :
                            "Cannot delete supplier with " + productCount + " products"
            ));
        }
        return ResponseEntity.notFound().build();
    }
}
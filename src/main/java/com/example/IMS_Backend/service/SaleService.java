package com.example.IMS_Backend.service;

import com.example.IMS_Backend.dto.SaleDTO;
import com.example.IMS_Backend.dto.SaleResponseDTO;
import com.example.IMS_Backend.model.Product;
import com.example.IMS_Backend.model.Sale;
import com.example.IMS_Backend.model.Store;
import com.example.IMS_Backend.repository.ProductRepository;
import com.example.IMS_Backend.repository.SaleRepository;
import com.example.IMS_Backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private InventoryService inventoryService; // Add this

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Convert Sale entity → DTO
    private SaleResponseDTO convertToDTO(Sale sale) {
        String categoryName = (sale.getProduct().getCategory() != null)
                ? sale.getProduct().getCategory().getName()
                : "N/A";

        return new SaleResponseDTO(
                sale.getId(),
                sale.getProduct().getName(),
                categoryName,
                sale.getStore().getName(),
                sale.getQuantity(),
                sale.getPrice(),
                dateFormat.format(sale.getSaleDate()),
                sale.getDescription()
        );
    }

    // Get all sales
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get sale by ID
    public SaleResponseDTO getSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
        return convertToDTO(sale);
    }

    // Create sale - ENHANCED with inventory update
    public SaleResponseDTO createSaleFromDTO(SaleDTO dto) throws ParseException {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Check inventory availability using InventoryService
        if (!inventoryService.hasSufficientInventory(dto.getStoreId(), dto.getProductId(), dto.getQuantity())) {
            Integer currentStock = inventoryService.getCurrentStockLevel(dto.getStoreId(), dto.getProductId());
            throw new RuntimeException("Insufficient inventory. Available: " + currentStock + ", Requested: " + dto.getQuantity());
        }

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setStore(store);
        sale.setQuantity(dto.getQuantity());
        sale.setPrice(dto.getPrice());
        sale.setSaleDate(dateFormat.parse(dto.getSaleDate()));
        sale.setDescription(dto.getDescription());

        Sale savedSale = saleRepository.save(sale);

        // Update product quantity

        productRepository.save(product);

        // AUTO-UPDATE INVENTORY
        inventoryService.updateInventoryAfterSale(
                dto.getStoreId(),
                dto.getProductId(),
                dto.getQuantity()
        );

        return convertToDTO(savedSale);
    }

    // Update sale - ENHANCED with inventory update
    public SaleResponseDTO updateSaleFromDTO(Long id, SaleDTO dto) throws ParseException {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));

        Product oldProduct = sale.getProduct();
        Product newProduct = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Calculate quantity difference
        int quantityDiff = dto.getQuantity() - sale.getQuantity();

        // Adjust stock if product changed
        if (!oldProduct.getId().equals(newProduct.getId())) {
            // Restore old product stock

            productRepository.save(oldProduct);

            // Check inventory for new product
            if (!inventoryService.hasSufficientInventory(dto.getStoreId(), dto.getProductId(), dto.getQuantity())) {
                Integer currentStock = inventoryService.getCurrentStockLevel(dto.getStoreId(), dto.getProductId());
                throw new RuntimeException("Insufficient inventory for product: " + newProduct.getName() + ". Available: " + currentStock);
            }

            // Update new product stock
            productRepository.save(newProduct);

            // AUTO-UPDATE INVENTORY for both products
            inventoryService.updateInventoryAfterSaleOrReturn(
                    sale.getStore().getId(),
                    oldProduct.getId(),
                    sale.getQuantity()
            );
            inventoryService.updateInventoryAfterSale(
                    dto.getStoreId(),
                    newProduct.getId(),
                    dto.getQuantity()
            );

        } else {
            // Same product: adjust by difference in quantity
            if (quantityDiff > 0) {
                // Selling more - check inventory
                if (!inventoryService.hasSufficientInventory(dto.getStoreId(), dto.getProductId(), quantityDiff)) {
                    Integer currentStock = inventoryService.getCurrentStockLevel(dto.getStoreId(), dto.getProductId());
                    throw new RuntimeException("Insufficient inventory. Available: " + currentStock + ", Additional needed: " + quantityDiff);
                }
                // AUTO-UPDATE INVENTORY for additional sale
                inventoryService.updateInventoryAfterSale(
                        dto.getStoreId(),
                        dto.getProductId(),
                        quantityDiff
                );
            } else if (quantityDiff < 0) {
                // Selling less - restore inventory
                inventoryService.updateInventoryAfterReturn(
                        dto.getStoreId(),
                        dto.getProductId(),
                        Math.abs(quantityDiff)
                );
            }


            productRepository.save(newProduct);
        }

        sale.setProduct(newProduct);
        sale.setStore(store);
        sale.setQuantity(dto.getQuantity());
        sale.setPrice(dto.getPrice());
        sale.setSaleDate(dateFormat.parse(dto.getSaleDate()));
        sale.setDescription(dto.getDescription());

        Sale updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    // Delete sale - ENHANCED with inventory update
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));

        // Restore product stock
        Product product = sale.getProduct();

        productRepository.save(product);

        // AUTO-UPDATE INVENTORY (reverse the sale)
        inventoryService.updateInventoryAfterReturn(
                sale.getStore().getId(),
                sale.getProduct().getId(),
                sale.getQuantity()
        );

        saleRepository.deleteById(id);
    }
}
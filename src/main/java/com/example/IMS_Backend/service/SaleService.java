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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Convert Sale entity → DTO
    private SaleResponseDTO convertToDTO(Sale sale) {
        return new SaleResponseDTO(
                sale.getId(),
                sale.getProduct().getName(),
                sale.getStore().getName(),
                sale.getQuantity(),
                sale.getPrice(),
                dateFormat.format(sale.getSaleDate()),
                sale.getDescription()
        );
    }

    // Get all sales (DTO-based)
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get single sale (DTO-based)
    public SaleResponseDTO getSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return convertToDTO(sale);
    }

    // Create sale from SaleDTO and return DTO
    public SaleResponseDTO createSaleFromDTO(SaleDTO dto) throws ParseException {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setStore(store);
        sale.setQuantity(dto.getQuantity());
        sale.setPrice(dto.getPrice());
        sale.setSaleDate(dateFormat.parse(dto.getSaleDate()));
        sale.setDescription(dto.getDescription());

        Sale savedSale = saleRepository.save(sale);
        return convertToDTO(savedSale);
    }

    // Update sale from DTO and return DTO
    public SaleResponseDTO updateSaleFromDTO(Long id, SaleDTO dto) throws ParseException {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        sale.setProduct(product);
        sale.setStore(store);
        sale.setQuantity(dto.getQuantity());
        sale.setPrice(dto.getPrice());
        sale.setSaleDate(dateFormat.parse(dto.getSaleDate()));
        sale.setDescription(dto.getDescription());

        Sale updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    // Delete sale
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }
}

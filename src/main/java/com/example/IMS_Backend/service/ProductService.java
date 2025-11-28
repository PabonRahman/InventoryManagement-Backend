package com.example.IMS_Backend.service;

import com.example.IMS_Backend.config.ResourceNotFoundException;
import com.example.IMS_Backend.dto.ProductDTO;
import com.example.IMS_Backend.model.*;
import com.example.IMS_Backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final String UPLOAD_DIR = "uploads/";

    // Get all products as DTO
    public List<ProductDTO> getAllProductsDTO() {
        return productRepository.findAll()
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Get product by ID as DTO
    public ProductDTO getProductDTOById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return new ProductDTO(product);
    }

    // Create product with DTO and optional image
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile imageFile) throws IOException {
        Product product = convertToEntity(productDTO);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            product.setImageUrl(imageUrl);
        }

        Product saved = productRepository.save(product);
        return new ProductDTO(saved);
    }

    // Update product with DTO and optional image
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update basic fields
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());



        // Update relationships
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + productDTO.getSupplierId()));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }

        if (productDTO.getStoreId() != null) {
            Store store = storeRepository.findById(productDTO.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + productDTO.getStoreId()));
            product.setStore(store);
        } else {
            product.setStore(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl(saveImage(imageFile));
        }

        Product updated = productRepository.save(product);
        return new ProductDTO(updated);
    }

    // Update product without image (for regular API calls)
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        try {
            return updateProduct(id, productDTO, null);
        } catch (IOException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    // Convert DTO to Entity
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());


        product.setImageUrl(dto.getImageUrl());

        // Set relationships if IDs are provided
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.getSupplierId()));
            product.setSupplier(supplier);
        }

        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + dto.getStoreId()));
            product.setStore(store);
        }

        return product;
    }

    // Delete product
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        try {
            productRepository.delete(product);
        } catch (DataIntegrityViolationException ex) {
            throw ex; // Let a global exception handler manage DB constraints
        }
    }

    // Save image and return URL
    private String saveImage(MultipartFile imageFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + fileName;
    }
}
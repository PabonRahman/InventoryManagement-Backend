package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.SaleDTO;
import com.example.IMS_Backend.dto.SaleResponseDTO;
import com.example.IMS_Backend.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {

    @Autowired
    private SaleService saleService;

    // Get all sales (DTO response)
    @GetMapping
    public List<SaleResponseDTO> getAllSales() {
        return saleService.getAllSales();
    }

    // Get one sale by ID
    @GetMapping("/{id}")
    public SaleResponseDTO getSale(@PathVariable Long id) {
        return saleService.getSale(id);
    }

    // Create a new sale
    @PostMapping
    public SaleResponseDTO createSale(@RequestBody SaleDTO dto) throws ParseException {
        return saleService.createSaleFromDTO(dto);
    }

    // Update existing sale
    @PutMapping("/{id}")
    public SaleResponseDTO updateSale(@PathVariable Long id, @RequestBody SaleDTO dto) throws ParseException {
        return saleService.updateSaleFromDTO(id, dto);
    }

    // Delete a sale
    @DeleteMapping("/{id}")
    public void deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
    }
}

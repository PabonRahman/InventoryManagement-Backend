package com.example.IMS_Backend.service;

import com.example.IMS_Backend.model.Sale;
import com.example.IMS_Backend.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSale(Long id) {
        return saleRepository.findById(id).orElse(null);
    }

    public Sale createSale(Sale sale) {
        return saleRepository.save(sale);
    }

    public Sale updateSale(Long id, Sale updatedSale) {
        return saleRepository.findById(id).map(sale -> {
            sale.setProduct(updatedSale.getProduct());
            sale.setQuantity(updatedSale.getQuantity());
            sale.setPrice(updatedSale.getPrice());
            sale.setSaleDate(updatedSale.getSaleDate());
            sale.setDescription(updatedSale.getDescription());
            return saleRepository.save(sale);
        }).orElse(null);
    }

    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }
}

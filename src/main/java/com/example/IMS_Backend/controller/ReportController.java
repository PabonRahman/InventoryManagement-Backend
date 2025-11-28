package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.SaleResponseDTO;
import com.example.IMS_Backend.service.JasperReportService;
import com.example.IMS_Backend.service.SaleService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final JasperReportService reportService;
    @Autowired
    private SaleService saleService;


    public ReportController(JasperReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<byte[]> getSalesReport() throws JRException {
        // Example: fetch all sales from DB and map to DTO
        List<SaleResponseDTO> sales = fetchSalesFromDb(); // Implement this method

        Map<String, Object> params = new HashMap<>();
        params.put("ReportTitle", "Sales Report");

        JasperPrint jasperPrint = reportService.generateSaleReport(sales, params);
        byte[] pdfBytes = reportService.exportReportToPdf(jasperPrint);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=sales_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private List<SaleResponseDTO> fetchSalesFromDb() {
        // TODO: fetch from SaleRepository and map to SaleResponseDTO
        return saleService.getAllSales();
    }
}

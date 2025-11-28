package com.example.IMS_Backend.service;

import com.example.IMS_Backend.dto.SaleResponseDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    public JasperPrint generateSaleReport(List<SaleResponseDTO> sales, Map<String, Object> params) throws JRException {
        // Load JRXML template
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/sale_report.jrxml")
        );

        // Convert list to JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(sales);

        // Fill report
        return JasperFillManager.fillReport(jasperReport, params, dataSource);
    }

    public byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.dto.CreatePaymentDTO;

import com.example.IMS_Backend.dto.PaymentDTO;
import com.example.IMS_Backend.dto.UpdatePaymentDTO;

import com.example.IMS_Backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody CreatePaymentDTO createPaymentDTO) {
        PaymentDTO createdPayment = paymentService.createPayment(createPaymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentDTO updatePaymentDTO) {
        PaymentDTO updatedPayment = paymentService.updatePayment(id, updatePaymentDTO);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByEmployee(@PathVariable Long employeeId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByEmployee(employeeId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<PaymentDTO> payments = paymentService.getPaymentsByDateRange(start, end);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentDTO> processPayment(@PathVariable Long id) {
        PaymentDTO processedPayment = paymentService.processPayment(id);
        return ResponseEntity.ok(processedPayment);
    }

    @GetMapping("/employee/{employeeId}/total")
    public ResponseEntity<Double> getTotalPaymentsByEmployee(@PathVariable Long employeeId) {
        Double total = paymentService.getTotalPaymentsByEmployee(employeeId);
        return ResponseEntity.ok(total);
    }
}
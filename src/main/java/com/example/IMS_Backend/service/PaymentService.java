package com.example.IMS_Backend.service;



import com.example.IMS_Backend.dto.UpdatePaymentDTO;
import com.example.IMS_Backend.dto.CreatePaymentDTO;
import com.example.IMS_Backend.dto.PaymentDTO;
import com.example.IMS_Backend.model.Employee;

import com.example.IMS_Backend.model.Payment;

import com.example.IMS_Backend.repository.EmployeeRepository;

import com.example.IMS_Backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return convertToDTO(payment);
    }

    public PaymentDTO createPayment(CreatePaymentDTO createPaymentDTO) {
        Employee employee = employeeRepository.findById(createPaymentDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + createPaymentDTO.getEmployeeId()));

        // Validate employee is active
        if (!employee.getIsActive()) {
            throw new IllegalArgumentException("Cannot create payment for inactive employee");
        }

        Payment payment = new Payment();
        payment.setEmployee(employee);
        payment.setAmount(createPaymentDTO.getAmount());
        payment.setPaymentType(createPaymentDTO.getPaymentType());
        payment.setDescription(createPaymentDTO.getDescription());
        payment.setPaymentMethod(createPaymentDTO.getPaymentMethod());
        payment.setReferenceNumber(createPaymentDTO.getReferenceNumber());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    public PaymentDTO updatePayment(Long id, UpdatePaymentDTO updatePaymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        if (updatePaymentDTO.getAmount() != null) {
            payment.setAmount(updatePaymentDTO.getAmount());
        }
        if (updatePaymentDTO.getPaymentType() != null) {
            payment.setPaymentType(updatePaymentDTO.getPaymentType());
        }
        if (updatePaymentDTO.getDescription() != null) {
            payment.setDescription(updatePaymentDTO.getDescription());
        }
        if (updatePaymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(updatePaymentDTO.getPaymentMethod());
        }
        if (updatePaymentDTO.getStatus() != null) {
            payment.setStatus(updatePaymentDTO.getStatus());
        }
        if (updatePaymentDTO.getReferenceNumber() != null) {
            payment.setReferenceNumber(updatePaymentDTO.getReferenceNumber());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    public List<PaymentDTO> getPaymentsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Employee not found with id: " + employeeId);
        }

        return paymentRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        // Simulate payment processing
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        Payment processedPayment = paymentRepository.save(payment);
        return convertToDTO(processedPayment);
    }

    public Double getTotalPaymentsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Employee not found with id: " + employeeId);
        }

        Double total = paymentRepository.getTotalPaidAmountByEmployee(employeeId);
        return total != null ? total : 0.0;
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setEmployeeId(payment.getEmployee().getId());
        dto.setEmployeeName(payment.getEmployee().getFirstName() + " " + payment.getEmployee().getLastName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentType(payment.getPaymentType());
        dto.setDescription(payment.getDescription());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
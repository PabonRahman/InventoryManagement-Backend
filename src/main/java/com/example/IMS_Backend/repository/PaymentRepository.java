package com.example.IMS_Backend.repository;


import com.example.IMS_Backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByEmployeeId(Long employeeId);

    List<Payment> findByStatus(String status);

    List<Payment> findByPaymentType(String paymentType);

    List<Payment> findByPaymentMethod(String paymentMethod);

    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.employee.id = :employeeId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByEmployeeAndDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.employee.id = :employeeId AND p.status = 'COMPLETED'")
    Double getTotalPaidAmountByEmployee(Long employeeId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
    Double getTotalPaymentsInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Payment p JOIN p.employee e WHERE e.firstName LIKE %:employeeName% OR e.lastName LIKE %:employeeName%")
    List<Payment> findByEmployeeNameContaining(String employeeName);
}
package com.example.IMS_Backend.service;



import com.example.IMS_Backend.dto.CreateEmployeeDTO;
import com.example.IMS_Backend.dto.EmployeeDTO;
import com.example.IMS_Backend.dto.UpdateEmployeeDTO;

import com.example.IMS_Backend.model.Employee;
import com.example.IMS_Backend.repository.EmployeeRepository;
import com.example.IMS_Backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(createEmployeeDTO.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + createEmployeeDTO.getEmail() + " already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(createEmployeeDTO.getFirstName());
        employee.setLastName(createEmployeeDTO.getLastName());
        employee.setEmail(createEmployeeDTO.getEmail());
        employee.setPhoneNumber(createEmployeeDTO.getPhoneNumber());
        employee.setPosition(createEmployeeDTO.getPosition());

        employee.setSalary(createEmployeeDTO.getSalary());
        employee.setHireDate(LocalDateTime.now());
        employee.setIsActive(true);

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO updateEmployeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        if (updateEmployeeDTO.getFirstName() != null) {
            employee.setFirstName(updateEmployeeDTO.getFirstName());
        }
        if (updateEmployeeDTO.getLastName() != null) {
            employee.setLastName(updateEmployeeDTO.getLastName());
        }
        if (updateEmployeeDTO.getEmail() != null && !employee.getEmail().equals(updateEmployeeDTO.getEmail())) {
            if (employeeRepository.existsByEmail(updateEmployeeDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updateEmployeeDTO.getEmail());
            }
            employee.setEmail(updateEmployeeDTO.getEmail());
        }
        if (updateEmployeeDTO.getPhoneNumber() != null) {
            employee.setPhoneNumber(updateEmployeeDTO.getPhoneNumber());
        }
        if (updateEmployeeDTO.getPosition() != null) {
            employee.setPosition(updateEmployeeDTO.getPosition());
        }
        if (updateEmployeeDTO.getDepartment() != null) {
            employee.setDepartment(updateEmployeeDTO.getDepartment());
        }
        if (updateEmployeeDTO.getSalary() != null) {
            employee.setSalary(updateEmployeeDTO.getSalary());
        }
        if (updateEmployeeDTO.getIsActive() != null) {
            employee.setIsActive(updateEmployeeDTO.getIsActive());
            if (!updateEmployeeDTO.getIsActive()) {
                employee.setTerminationDate(LocalDateTime.now());
            }
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setPosition(employee.getPosition());
        dto.setDepartment(employee.getDepartment());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        dto.setTerminationDate(employee.getTerminationDate());
        dto.setIsActive(employee.getIsActive());
        return dto;
    }
}
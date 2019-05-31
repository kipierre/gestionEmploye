package org.gestionemploye.service;


import org.gestionemploye.dto.EmployeeDTO;
import org.gestionemploye.entity.Employee;
import org.gestionemploye.exceptions.DataConflictException;
import org.gestionemploye.exceptions.DataNotFoundException;
import org.gestionemploye.mapper.EmployeeMapper;
import org.gestionemploye.repository.EmployeeRepository;
import org.gestionemploye.security.ExtractUserAuthentication;
import org.gestionemploye.util.ExceptionConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    public EmployeeService(final EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all Employees Record
     *
     * @return List of Employee
     */

    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {

        return employeeRepository.findAll(pageable)
                .map(employee -> EmployeeMapper.mapToDTOWithSupervisor(employee));
    }

    /**
     * Get single Employee Record
     *
     * @param id
     * @return If present Employee else throws Exception
     */

    public EmployeeDTO getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionConstants.EMPLOYEE_RECORD_NOT_FOUND));
        return EmployeeMapper.mapToDTOWithSupervisor(employee);
    }


    public EmployeeDTO retrieveAuthenticatedEmployee() {

        long authenticatedEmployeeId = ExtractUserAuthentication.getCurrentUser().getEmployeeId();
        Employee employee = employeeRepository.findById(authenticatedEmployeeId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionConstants.EMPLOYEE_RECORD_NOT_FOUND));
        return EmployeeMapper.mapToDTOWithSupervisor(employee);
    }

    /**
     * Create New Employee
     * If EmployeeSupervisor id is sent but id doesn't exist in database then throws Exception
     *
     * @param employeeDTO
     * @return saved Employee
     */

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {

        //  EmployeeSupervisor id is sent but id doesn't exist in database
        if (employeeDTO.getSupervisor() != null &&
                employeeDTO.getSupervisor().getEmployeeId() != null &&
                !employeeRepository.findById(employeeDTO.getSupervisor().getEmployeeId()).isPresent()) {
            throw new DataNotFoundException(ExceptionConstants.EMPLOYEE_SUPERVISOR_MISMATCH);
        }
        if (employeeDTO.getUsername() == null || employeeRepository.findByUsername(employeeDTO.getUsername()) != null) {
            throw new DataNotFoundException(ExceptionConstants.EMPLOYEE_USERNAME_NOT_VALID);
        }

        employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employeeDTO.setRole("ROLE_USER");
        employeeDTO.setPhoneNumber(employeeDTO.getPhoneNumber());
        employeeDTO.setStatus(employeeDTO.getStatus());
        Employee employee = employeeRepository.save(EmployeeMapper.mapToEntityWithSupervisor(employeeDTO));
        return EmployeeMapper.mapToDto(employee);
    }

    /**
     * Update Employee
     * Employee must be present in database else throws Exception
     * Employee cannot be their own Supervisor and EmployeeSupervisor id must be present in database else throws Exception
     * Can only update Employee FullName, Email and EmployeeSupervisor
     *
     * @param employeeDTO
     * @return updated Employee
     */

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {

        Employee returnedEmployee = employeeRepository.findById(employeeDTO.getEmployeeId())
                .orElseThrow(() -> new DataNotFoundException(ExceptionConstants.EMPLOYEE_RECORD_NOT_FOUND));

        // Employee cannot be their own Supervisor and EmployeeSupervisor must be present in database
        if ((employeeDTO.getSupervisor() != null &&
                employeeDTO.getSupervisor().getEmployeeId() != null) &&
                (returnedEmployee.getEmployeeId() == employeeDTO.getSupervisor().getEmployeeId()
                        || !employeeRepository.findById(employeeDTO.getSupervisor().getEmployeeId()).isPresent())) {
            throw new DataConflictException(ExceptionConstants.EMPLOYEE_SUPERVISOR_MISMATCH);
        }
        returnedEmployee.setFirstName(employeeDTO.getFirstName());
        returnedEmployee.setMiddleName(employeeDTO.getMiddleName());
        returnedEmployee.setLastName(employeeDTO.getLastName());
        returnedEmployee.setEmail(employeeDTO.getEmail());
        returnedEmployee.setPhoneNumber(employeeDTO.getPhoneNumber());
        if (employeeDTO.getSupervisor() != null) {
            returnedEmployee.setSupervisor(EmployeeMapper.mapToEntity(employeeDTO.getSupervisor()));
        } else {
            returnedEmployee.setSupervisor(null);
        }
        return EmployeeMapper.mapToDto(employeeRepository.save(returnedEmployee));
    }


    public EmployeeDTO updatePassword(String oldPassword, String newPassword) {

        long authenticatedEmployeeId = ExtractUserAuthentication.getCurrentUser().getEmployeeId();
        Employee employee = employeeRepository.findById(authenticatedEmployeeId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionConstants.EMPLOYEE_RECORD_NOT_FOUND));
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new DataNotFoundException(ExceptionConstants.OLD_PASSWORD_DOESNT_MATCH);
        }
        employee.setPassword(passwordEncoder.encode(newPassword));
        return EmployeeMapper.mapToDto(employeeRepository.save(employee));
    }


    public List<EmployeeDTO> getAllEmployeeUnderSupervision(Long id) {

        Employee returnedEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionConstants.EMPLOYEE_RECORD_NOT_FOUND));

        return employeeRepository.findAllBySupervisor(returnedEmployee)
                .stream()
                .map(employee -> EmployeeMapper.mapToDto(employee))
                .collect(Collectors.toList());
    }


    public Page<EmployeeDTO> getAllEmployeesByName(Pageable pageable, String fullName) {

        return employeeRepository.findByFirstNameContainingOrMiddleNameContainingOrLastNameContaining(pageable, fullName, fullName, fullName)
                .map(employee -> EmployeeMapper.mapToDTOWithSupervisor(employee));
    }
}

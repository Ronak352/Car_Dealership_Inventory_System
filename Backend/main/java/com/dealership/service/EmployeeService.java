package com.dealership.service;

import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.enums.Role;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    void deleteEmployee(Long id);

    EmployeeResponse getEmployeeById(Long id);

    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> searchEmployees(String keyword);

    EmployeeResponse assignRole(Long employeeId, Role role);
}
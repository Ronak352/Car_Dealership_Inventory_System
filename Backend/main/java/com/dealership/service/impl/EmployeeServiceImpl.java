package com.dealership.service.impl;

import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.entity.Employee;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.EmployeeRepository;
import com.dealership.repository.UserRepository;
import com.dealership.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (employeeRepository.findByEmployeeCode(request.getEmployeeCode()).isPresent()) {
            throw new DuplicateResourceException("Employee code already exists.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + request.getUserId()));

        user.setRole(request.getRole());

        Employee employee = Employee.builder()
                .user(user)
                .employeeCode(request.getEmployeeCode())
                .joiningDate(request.getJoiningDate())
                .salary(request.getSalary())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id : " + id));

        if (!employee.getEmployeeCode().equals(request.getEmployeeCode())
                && employeeRepository.findByEmployeeCode(request.getEmployeeCode()).isPresent()) {
            throw new DuplicateResourceException("Employee code already exists.");
        }

        User user = employee.getUser();

        user.setRole(request.getRole());

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setSalary(request.getSalary());

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id : " + id));

        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id : " + id));

        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> searchEmployees(String keyword) {

        return employeeRepository
                .findByEmployeeCodeContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EmployeeResponse assignRole(Long employeeId, Role role) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id : " + employeeId));

        employee.getUser().setRole(role);

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        User user = employee.getUser();

        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(user.getId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .joiningDate(employee.getJoiningDate())
                .salary(employee.getSalary())
                .role(user.getRole().name())
                .build();
    }
}
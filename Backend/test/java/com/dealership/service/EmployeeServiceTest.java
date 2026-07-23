package com.dealership.service;

import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.entity.Employee;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.EmployeeRepository;
import com.dealership.repository.UserRepository;
import com.dealership.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {

        EmployeeRequest request = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .phone("9999999999")
                .role(Role.CUSTOMER)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(request.getJoiningDate())
                .salary(request.getSalary())
                .build();

        when(employeeRepository.findByEmployeeCode("EMP001"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeCode()).isEqualTo("EMP001");

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldThrowDuplicateEmployeeCodeException() {

        EmployeeRequest request = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        when(employeeRepository.findByEmployeeCode("EMP001"))
                .thenReturn(Optional.of(new Employee()));

        assertThatThrownBy(() ->
                employeeService.createEmployee(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowUserNotFoundException() {

        EmployeeRequest request = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        when(employeeRepository.findByEmployeeCode("EMP001"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                employeeService.createEmployee(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void shouldGetEmployeeById() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .phone("9999999999")
                .role(Role.SALESPERSON)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeCode()).isEqualTo("EMP001");
    }

    @Test
    void shouldReturnAllEmployees() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .phone("9999999999")
                .role(Role.SALESPERSON)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .build();

        when(employeeRepository.findAll())
                .thenReturn(List.of(employee));

        List<EmployeeResponse> employees = employeeService.getAllEmployees();

        assertThat(employees).hasSize(1);
    }

    @Test
    void shouldDeleteEmployee() {

        Employee employee = Employee.builder()
                .id(1L)
                .employeeCode("EMP001")
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void shouldUpdateEmployee() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .role(Role.CUSTOMER)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(40000))
                .build();

        EmployeeRequest request = EmployeeRequest.builder()
                .employeeCode("EMP002")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(60000))
                .role(Role.MANAGER)
                .build();

        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP002")
                .joiningDate(request.getJoiningDate())
                .salary(request.getSalary())
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.findByEmployeeCode("EMP002"))
                .thenReturn(Optional.empty());

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(updatedEmployee);

        EmployeeResponse response = employeeService.updateEmployee(1L, request);

        assertThat(response.getEmployeeCode()).isEqualTo("EMP002");
        assertThat(response.getRole()).isEqualTo("MANAGER");

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldAssignRole() {

        User user = User.builder()
                .id(1L)
                .role(Role.CUSTOMER)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeResponse response =
                employeeService.assignRole(1L, Role.MANAGER);

        assertThat(response.getRole()).isEqualTo("MANAGER");
    }

    @Test
    void shouldSearchEmployees() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .phone("9999999999")
                .role(Role.SALESPERSON)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .build();

        when(employeeRepository
                .findByEmployeeCodeContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()))
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.searchEmployees("john");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEmployeeCode()).isEqualTo("EMP001");
    }
}
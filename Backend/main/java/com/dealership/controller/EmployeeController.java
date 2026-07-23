package com.dealership.controller;

import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.enums.Role;
import com.dealership.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee/salesperson management: create, update, search, role assignment and lookups.")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {


    private final EmployeeService employeeService;



    @Operation(summary = "Create an employee", description = "Onboards a new employee record linked to a user account. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Employee code already in use")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request
    ){

        return new ResponseEntity<>(
                employeeService.createEmployee(request),
                HttpStatus.CREATED
        );
    }



    @Operation(summary = "Update an employee", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request
    ){

        return ResponseEntity.ok(
                employeeService.updateEmployee(id, request)
        );
    }



    @Operation(summary = "Delete an employee", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id
    ){

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(
                "Employee deleted successfully"
        );
    }




    @Operation(summary = "Get an employee by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                employeeService.getEmployeeById(id)
        );
    }




    @Operation(summary = "List all employees")
    @ApiResponse(responseCode = "200", description = "All employees")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(){

        return ResponseEntity.ok(
                employeeService.getAllEmployees()
        );
    }




    @Operation(summary = "Search employees by keyword", description = "Matches against name / employee code / email depending on the service implementation.")
    @ApiResponse(responseCode = "200", description = "Matching employees (may be empty)")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword
    ){

        return ResponseEntity.ok(
                employeeService.searchEmployees(keyword)
        );
    }





    @Operation(summary = "Assign a role to an employee", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> assignRole(
            @PathVariable Long id,
            @RequestParam Role role
    ){

        return ResponseEntity.ok(
                employeeService.assignRole(id, role)
        );
    }

}
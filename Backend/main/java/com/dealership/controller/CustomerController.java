package com.dealership.controller;


import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.response.CustomerResponse;

import com.dealership.service.CustomerService;

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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer profiles linked to a user account.")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {



    private final CustomerService customerService;




    @Operation(summary = "Create a customer profile", description = "Attaches address/city/state/pincode details to an existing user account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer profile created"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<CustomerResponse> createCustomer(
            @PathVariable Long userId,
            @Valid @RequestBody CustomerRequest request){


        return new ResponseEntity<>(

            customerService.createCustomer(
                    userId,
                    request),

            HttpStatus.CREATED
        );

    }





    @Operation(summary = "Get a customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Long id){


        return ResponseEntity.ok(
            customerService.getCustomerById(id)
        );

    }





    @Operation(summary = "Get a customer by their linked user id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<CustomerResponse> getByUserId(
            @PathVariable Long userId){


        return ResponseEntity.ok(
            customerService.getCustomerByUserId(userId)
        );

    }





    @Operation(summary = "List all customers")
    @ApiResponse(responseCode = "200", description = "All customers")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<CustomerResponse>> getAll(){


        return ResponseEntity.ok(
            customerService.getAllCustomers()
        );

    }





    @Operation(summary = "Update a customer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request){


        return ResponseEntity.ok(
            customerService.updateCustomer(
                    id,
                    request)
        );

    }





    @Operation(summary = "Delete a customer profile", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(
            @PathVariable Long id){


        customerService.deleteCustomer(id);


        return ResponseEntity.ok(
            "Customer deleted successfully"
        );

    }

}
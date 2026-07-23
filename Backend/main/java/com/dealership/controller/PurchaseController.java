package com.dealership.controller;


import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.PurchaseResponse;
import com.dealership.service.PurchaseService;

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
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Vehicle purchases and purchase history: create a sale and look it up by customer, vehicle or salesperson.")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseController {



    private final PurchaseService purchaseService;



    // ======================================
    // CREATE PURCHASE
    // ======================================

    @Operation(
            summary = "Create a purchase",
            description = "Records a vehicle purchase: creates the purchase record, decrements inventory, and generates the associated payment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Purchase created"),
            @ApiResponse(responseCode = "400", description = "Vehicle unavailable or validation failed"),
            @ApiResponse(responseCode = "404", description = "Customer or vehicle not found")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<PurchaseResponse> createPurchase(

            @Valid @RequestBody PurchaseRequest request

    ) {


        PurchaseResponse response =
                purchaseService.createPurchase(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }





    // ======================================
    // GET PURCHASE BY ID
    // ======================================

    @Operation(summary = "Get a purchase by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase found"),
            @ApiResponse(responseCode = "404", description = "Purchase not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<PurchaseResponse> getPurchaseById(

            @PathVariable Long id

    ) {


        return ResponseEntity.ok(

                purchaseService.getPurchaseById(id)

        );

    }





    // ======================================
    // GET PURCHASES BY CUSTOMER
    // ======================================

    @Operation(
            summary = "Get purchase history for a customer",
            description = "Returns every purchase made by the given customer, most useful for a customer's 'My Orders' view."
    )
    @ApiResponse(responseCode = "200", description = "Purchase history (may be empty)")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<List<PurchaseResponse>> getPurchasesByCustomer(

            @PathVariable Long customerId

    ) {


        return ResponseEntity.ok(

                purchaseService
                        .getPurchasesByCustomer(customerId)

        );

    }





    // ======================================
    // GET PURCHASES BY VEHICLE
    // ======================================

    @Operation(summary = "Get purchase history for a vehicle")
    @ApiResponse(responseCode = "200", description = "Purchase history for this vehicle (may be empty)")
    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON')")
    public ResponseEntity<List<PurchaseResponse>> getPurchasesByVehicle(

            @PathVariable Long vehicleId

    ) {


        return ResponseEntity.ok(

                purchaseService
                        .getPurchasesByVehicle(vehicleId)

        );

    }





    // ======================================
    // GET PURCHASES BY SALESPERSON
    // ======================================

    @Operation(summary = "Get sales made by a salesperson")
    @ApiResponse(responseCode = "200", description = "Purchases attributed to this salesperson (may be empty)")
    @GetMapping("/salesperson/{salespersonId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON')")
    public ResponseEntity<List<PurchaseResponse>> getPurchasesBySalesperson(

            @PathVariable Long salespersonId

    ) {


        return ResponseEntity.ok(

                purchaseService
                        .getPurchasesBySalesperson(salespersonId)

        );

    }

}
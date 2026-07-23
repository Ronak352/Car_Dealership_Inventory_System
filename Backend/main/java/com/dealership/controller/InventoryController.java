package com.dealership.controller;

import com.dealership.dto.response.InventoryResponse;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock movement: restock, sell down, adjust, and inspect inventory history/levels.")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    // ==========================================
    // INCREASE STOCK
    // ==========================================

    @Operation(summary = "Restock a vehicle", description = "Increases available quantity and logs an ADD inventory movement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock increased"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PostMapping("/increase/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryResponse> increaseStock(

            @PathVariable Long vehicleId,

            @RequestParam Integer quantity,

            @RequestParam Long performedByUserId

    ) {

        return ResponseEntity.ok(

                inventoryService.increaseStock(
                        vehicleId,
                        quantity,
                        performedByUserId
                )

        );

    }

    // ==========================================
    // DECREASE STOCK
    // ==========================================

    @Operation(summary = "Reduce vehicle stock", description = "Decreases available quantity and logs a REMOVE inventory movement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock decreased"),
            @ApiResponse(responseCode = "400", description = "Insufficient quantity available"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PostMapping("/decrease/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryResponse> decreaseStock(

            @PathVariable Long vehicleId,

            @RequestParam Integer quantity,

            @RequestParam Long performedByUserId

    ) {

        return ResponseEntity.ok(

                inventoryService.decreaseStock(
                        vehicleId,
                        quantity,
                        performedByUserId
                )

        );

    }

    // ==========================================
    // UPDATE STOCK
    // ==========================================

    @Operation(summary = "Set vehicle stock to an exact quantity", description = "Logs an UPDATE inventory movement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock updated"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PutMapping("/update/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryResponse> updateStock(

            @PathVariable Long vehicleId,

            @RequestParam Integer quantity,

            @RequestParam Long performedByUserId

    ) {

        return ResponseEntity.ok(

                inventoryService.updateStock(
                        vehicleId,
                        quantity,
                        performedByUserId
                )

        );

    }

    // ==========================================
    // INVENTORY HISTORY
    // ==========================================

    @Operation(summary = "Get inventory movement history for a vehicle", description = "Newest entries first.")
    @ApiResponse(responseCode = "200", description = "Inventory log entries (may be empty)")
    @GetMapping("/history/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<InventoryResponse>> getInventoryHistory(

            @PathVariable Long vehicleId

    ) {

        return ResponseEntity.ok(

                inventoryService.getInventoryHistory(vehicleId)

        );

    }

    // ==========================================
    // AVAILABLE QUANTITY
    // ==========================================

    @Operation(summary = "Get available quantity for a vehicle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current available quantity"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @GetMapping("/quantity/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<Integer> getAvailableQuantity(

            @PathVariable Long vehicleId

    ) {

        return ResponseEntity.ok(

                inventoryService.getAvailableQuantity(vehicleId)

        );

    }

    // ==========================================
    // LOW STOCK VEHICLES
    // ==========================================

    @Operation(summary = "List low-stock vehicles", description = "Returns vehicles whose quantity is at or below the given threshold (default 5).")
    @ApiResponse(responseCode = "200", description = "Vehicles at or below the threshold")
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<VehicleResponse>> getLowStockVehicles(

            @RequestParam(defaultValue = "5") Integer threshold

    ) {

        return ResponseEntity.ok(

                inventoryService.getLowStockVehicles(threshold)

        );

    }

}
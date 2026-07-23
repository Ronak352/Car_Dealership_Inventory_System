package com.dealership.controller;


import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.service.VehicleService;


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

import com.dealership.enums.VehicleCategory;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicle inventory: add, browse, search, update and delete listings.")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {



    private final VehicleService vehicleService;





    // ===============================
    // ADD VEHICLE
    // ===============================

    @Operation(
            summary = "Add a new vehicle",
            description = "Registers a new vehicle in the inventory. Restricted to ADMIN and MANAGER. The VIN number must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Duplicate VIN number")
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(

            @Valid
            @RequestBody VehicleRequest request

    ){

        VehicleResponse response =
                vehicleService.addVehicle(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }






    // ===============================
    // VIEW AVAILABLE VEHICLES
    // ===============================

    @Operation(
            summary = "List available vehicles",
            description = "Returns every vehicle currently in AVAILABLE status. Any authenticated role can view the catalog."
    )
    @ApiResponse(responseCode = "200", description = "List of available vehicles")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAvailableVehicles(){



        List<VehicleResponse> vehicles =

                vehicleService.getAvailableVehicles();



        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vehicles);


    }
    
 // ===============================
    // SEARCH VEHICLES
    // ===============================

    @Operation(
            summary = "Search vehicles",
            description = "Filters the catalog by any combination of brand, model, category, minimum price and maximum price. Omit a parameter to skip that filter."
    )
    @ApiResponse(responseCode = "200", description = "Matching vehicles (may be empty)")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponse>> searchVehicles(

            @RequestParam(required = false) String brand,

            @RequestParam(required = false) String model,

            @RequestParam(required = false) VehicleCategory category,

            @RequestParam(required = false) BigDecimal minPrice,

            @RequestParam(required = false) BigDecimal maxPrice

    ){


        List<VehicleResponse> vehicles =

                vehicleService.searchVehicles(
                        brand,
                        model,
                        category,
                        minPrice,
                        maxPrice
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vehicles);

    }
   //===============================
   //UPDATE VEHICLE
   //===============================

   @Operation(
           summary = "Update a vehicle",
           description = "Updates an existing vehicle's details. Restricted to ADMIN and MANAGER."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Vehicle updated"),
           @ApiResponse(responseCode = "400", description = "Validation failed"),
           @ApiResponse(responseCode = "404", description = "Vehicle not found")
   })
   @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
   @PutMapping("/{id}")
   public ResponseEntity<VehicleResponse> updateVehicle(

         @PathVariable Long id,

         @Valid
         @RequestBody VehicleRequest request

   ) {


     VehicleResponse response =

             vehicleService.updateVehicle(
                     id,
                     request
             );


     return ResponseEntity
             .status(HttpStatus.OK)
             .body(response);

   }

   //===============================
   //DELETE VEHICLE
   //===============================

   @Operation(
           summary = "Delete a vehicle",
           description = "Permanently removes a vehicle listing. ADMIN only."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "204", description = "Vehicle deleted"),
           @ApiResponse(responseCode = "404", description = "Vehicle not found")
   })
   @PreAuthorize("hasRole('ADMIN')")
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteVehicle(

        @PathVariable Long id

   ){


    vehicleService.deleteVehicle(id);


    return ResponseEntity
            .noContent()
            .build();

   }


}
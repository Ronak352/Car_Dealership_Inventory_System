package com.dealership.controller;


import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.TestDriveBookingResponse;


import com.dealership.service.TestDriveService;

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
@RequestMapping("/api/test-drives")
@RequiredArgsConstructor
@Tag(name = "Test Drives", description = "Book, assign, approve/reject and track test drive bookings.")
@SecurityRequirement(name = "bearerAuth")
public class TestDriveController {



    private final TestDriveService testDriveService;





    @Operation(summary = "Book a test drive", description = "Creates a test drive booking request for a customer and vehicle.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "404", description = "Customer or vehicle not found")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<TestDriveBookingResponse> createBooking(

            @Valid
            @RequestBody TestDriveBookingRequest request

    ){


        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(
                        testDriveService.createBooking(request)
                );

    }








    @Operation(summary = "Get a test drive booking by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<TestDriveBookingResponse> getBookingById(

            @PathVariable Long id

    ){


        return ResponseEntity.ok(

                testDriveService.getBookingById(id)

        );

    }









    @Operation(summary = "List all test drive bookings")
    @ApiResponse(responseCode = "200", description = "All bookings")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TestDriveBookingResponse>> getAllBookings(){


        return ResponseEntity.ok(

                testDriveService.getAllBookings()

        );

    }








    @Operation(summary = "Get test drive bookings for a customer")
    @ApiResponse(responseCode = "200", description = "Bookings for this customer (may be empty)")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<List<TestDriveBookingResponse>> getByCustomer(

            @PathVariable Long customerId

    ){


        return ResponseEntity.ok(

                testDriveService.getBookingsByCustomer(customerId)

        );

    }








    @Operation(summary = "Get test drive bookings for a vehicle")
    @ApiResponse(responseCode = "200", description = "Bookings for this vehicle (may be empty)")
    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON')")
    public ResponseEntity<List<TestDriveBookingResponse>> getByVehicle(

            @PathVariable Long vehicleId

    ){


        return ResponseEntity.ok(

                testDriveService.getBookingsByVehicle(vehicleId)

        );

    }








    @Operation(summary = "Assign a salesperson to a booking", description = "ADMIN/MANAGER only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salesperson assigned"),
            @ApiResponse(responseCode = "404", description = "Booking or salesperson not found")
    })
    @PutMapping("/{id}/assign/{salespersonId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TestDriveBookingResponse> assignSalesperson(

            @PathVariable Long id,

            @PathVariable Long salespersonId

    ){


        return ResponseEntity.ok(

                testDriveService.assignSalesperson(
                        id,
                        salespersonId
                )

        );

    }








    @Operation(
            summary = "Update a booking's status",
            description = "Transitions a booking, e.g. REQUESTED -> APPROVED -> COMPLETED, or CANCELLED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON')")
    public ResponseEntity<TestDriveBookingResponse> updateStatus(

            @PathVariable Long id,

            @RequestParam String status

    ){


        return ResponseEntity.ok(

                testDriveService.updateStatus(
                        id,
                        status
                )

        );

    }








    @Operation(summary = "Delete a test drive booking", description = "ADMIN/MANAGER only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking deleted"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteBooking(

            @PathVariable Long id

    ){


        testDriveService.deleteBooking(id);


        return ResponseEntity.ok().build();

    }

}
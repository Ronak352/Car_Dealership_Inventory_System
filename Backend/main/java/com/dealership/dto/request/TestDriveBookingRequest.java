package com.dealership.dto.request;


import jakarta.validation.constraints.NotNull;

import lombok.*;


import java.time.LocalDate;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveBookingRequest {


    @NotNull(message = "Customer ID is required")
    private Long customerId;


    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;


    private Long salespersonId;


    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;


    @NotNull(message = "Test drive date is required")
    private LocalDate testDriveDate;


}
package com.dealership.dto.response;


import com.dealership.enums.TestDriveStatus;

import lombok.*;


import java.time.LocalDate;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveBookingResponse {


    private Long id;


    private String customerName;


    private String vehicleName;


    private String salespersonName;


    private LocalDate bookingDate;


    private LocalDate testDriveDate;


    private TestDriveStatus status;


}
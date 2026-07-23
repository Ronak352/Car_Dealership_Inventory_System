package com.dealership.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {


    @NotBlank(message = "Address is required")
    private String address;


    @NotBlank(message = "City is required")
    private String city;


    @NotBlank(message = "State is required")
    private String state;


    @Pattern(
        regexp = "\\d{6}",
        message = "Pincode must contain 6 digits"
    )
    private String pincode;

}
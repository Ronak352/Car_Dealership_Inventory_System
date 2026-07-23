package com.dealership.dto.response;


import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleStatus;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {


    private Long id;


    private String brand;


    private String model;


    private String variant;


    private String vinNumber;


    private BigDecimal price;


    private Integer quantity;


    private VehicleCategory category;


    private VehicleStatus status;


}
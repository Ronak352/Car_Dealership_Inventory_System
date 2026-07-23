package com.dealership.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleImageResponse {


    private Long id;

    private Long vehicleId;

    private String imageUrl;
}
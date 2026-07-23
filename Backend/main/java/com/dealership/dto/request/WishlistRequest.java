package com.dealership.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistRequest {

    @NotNull(message = "Customer Id is required")
    private Long customerId;

    @NotNull(message = "Vehicle Id is required")
    private Long vehicleId;
}
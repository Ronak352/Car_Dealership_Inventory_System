package com.dealership.dto.response;

import com.dealership.enums.InventoryOperation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;

    private Long vehicleId;

    private String brand;

    private String model;

    private String vinNumber;

    private InventoryOperation operationType;

    private Integer quantity;

    // Current stock after the operation
    private Integer availableQuantity;

    private LocalDateTime date;

    private Long performedById;

    private String performedByName;
}
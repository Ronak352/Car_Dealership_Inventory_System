package com.dealership.dto.request;

import com.dealership.enums.FuelType;
import com.dealership.enums.Transmission;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    private String variant;

    @NotNull(message = "Category is required")
    private VehicleCategory category;

    private FuelType fuelType;

    private Transmission transmission;

    @Positive(message = "Manufacturing year must be positive")
    private Integer manufacturingYear;

    private String color;

    private String engineNumber;

    @NotBlank(message = "VIN Number is required")
    private String vinNumber;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @PositiveOrZero
    private BigDecimal discount;

    @PositiveOrZero
    private Integer quantity;

    @NotNull
    private VehicleCondition condition;

    @NotNull
    private VehicleStatus status;
}
package com.dealership.dto.request;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    private Long salespersonId;

    @NotNull
    private LocalDate purchaseDate;

    private LocalDate deliveryDate;

    @NotNull
    @Positive
    private BigDecimal sellingPrice;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private PurchaseStatus purchaseStatus;
}
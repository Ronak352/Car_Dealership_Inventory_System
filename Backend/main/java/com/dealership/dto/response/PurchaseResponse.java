package com.dealership.dto.response;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {

    private Long id;

    private String customerName;

    private String vehicleName;

    private LocalDate purchaseDate;

    private LocalDate deliveryDate;

    private BigDecimal sellingPrice;

    private PaymentMethod paymentMethod;

    private PurchaseStatus purchaseStatus;
}
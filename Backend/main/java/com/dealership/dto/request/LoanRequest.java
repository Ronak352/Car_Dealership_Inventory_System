package com.dealership.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequest {

    @NotNull(message = "Purchase Id is required")
    private Long purchaseId;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal loanAmount;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    private Integer tenure;
}
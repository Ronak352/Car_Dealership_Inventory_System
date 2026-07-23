package com.dealership.dto.response;

import com.dealership.enums.LoanApprovalStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {


    private Long id;

    private Long purchaseId;

    private String bankName;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private Integer tenure;

    private LoanApprovalStatus approvalStatus;
}
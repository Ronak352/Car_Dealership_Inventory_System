package com.dealership.dto.response;

import com.dealership.enums.LoanApprovalStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanResponseTest {

    @Test
    void shouldCreateLoanResponse() {

        LoanResponse response = LoanResponse.builder()
                .id(1L)
                .purchaseId(10L)
                .bankName("SBI")
                .loanAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("8.5"))
                .tenure(60)
                .approvalStatus(LoanApprovalStatus.APPROVED)
                .build();

        assertThat(response.getBankName()).isEqualTo("SBI");
        assertThat(response.getApprovalStatus())
                .isEqualTo(LoanApprovalStatus.APPROVED);
    }
}
package com.dealership.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateLoanRequest() {

        LoanRequest dto = LoanRequest.builder()
                .purchaseId(1L)
                .bankName("SBI")
                .loanAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("8.75"))
                .tenure(60)
                .build();

        assertThat(validator.validate(dto)).isEmpty();
    }
}
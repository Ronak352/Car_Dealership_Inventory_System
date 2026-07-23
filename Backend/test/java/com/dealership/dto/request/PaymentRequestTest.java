package com.dealership.dto.request;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldValidatePaymentRequest() {

        PaymentRequest dto = PaymentRequest.builder()
                .purchaseId(1L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN-123")
                .paymentDate(LocalDateTime.now())
                .build();

        assertThat(validator.validate(dto)).isEmpty();

    }

}

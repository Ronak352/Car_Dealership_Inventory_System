package com.dealership.dto.response;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    void shouldCreatePaymentResponse() {

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN123")
                .paymentDate(LocalDateTime.now())
                .build();

        assertThat(response.getTransactionId()).isEqualTo("TXN123");
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}

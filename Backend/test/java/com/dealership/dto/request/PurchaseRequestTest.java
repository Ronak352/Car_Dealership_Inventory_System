package com.dealership.dto.request;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldValidatePurchaseRequest() {

        PurchaseRequest dto = PurchaseRequest.builder()
                .customerId(1L)
                .vehicleId(1L)
                .purchaseDate(LocalDate.now())
                .sellingPrice(new BigDecimal("2200000"))
                .paymentMethod(PaymentMethod.CASH)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        assertThat(validator.validate(dto)).isEmpty();

    }

}

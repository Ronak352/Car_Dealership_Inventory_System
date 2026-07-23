package com.dealership.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWishlistRequest() {

        WishlistRequest dto = WishlistRequest.builder()
                .customerId(1L)
                .vehicleId(10L)
                .build();

        assertThat(validator.validate(dto)).isEmpty();
    }
}

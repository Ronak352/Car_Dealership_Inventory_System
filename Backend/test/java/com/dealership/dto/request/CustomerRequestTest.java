package com.dealership.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCustomerRequest() {

        CustomerRequest dto = CustomerRequest.builder()
                .address("Ahmedabad")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380001")
                .build();

        assertThat(validator.validate(dto)).isEmpty();
    }
}
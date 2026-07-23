package com.dealership.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {

        LoginRequest dto = LoginRequest.builder()
                .email("")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldPassWithValidData() {

        LoginRequest dto = LoginRequest.builder()
                .email("admin@test.com")
                .password("password123")
                .build();

        assertThat(validator.validate(dto)).isEmpty();
    }
}
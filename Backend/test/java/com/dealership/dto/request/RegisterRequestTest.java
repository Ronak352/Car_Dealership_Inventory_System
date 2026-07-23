package com.dealership.dto.request;

import com.dealership.enums.Role;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldValidateRegisterRequest() {

        RegisterRequest dto = RegisterRequest.builder()
                .firstName("Ronak")
                .lastName("Rathod")
                .email("ronak@test.com")
                .phone("9999999999")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        assertThat(validator.validate(dto)).isEmpty();

    }

}
package com.dealership.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TestDriveBookingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateTestDriveBookingRequest() {

        TestDriveBookingRequest request = TestDriveBookingRequest.builder()
                .customerId(1L)
                .vehicleId(101L)
                .salespersonId(5L)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(1))
                .build();

        Set<ConstraintViolation<TestDriveBookingRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenRequiredFieldsAreMissing() {

        TestDriveBookingRequest request = TestDriveBookingRequest.builder().build();

        Set<ConstraintViolation<TestDriveBookingRequest>> violations =
                validator.validate(request);

        assertThat(violations).hasSize(4);
    }
}
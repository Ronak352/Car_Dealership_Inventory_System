package com.dealership.dto.request;

import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void shouldValidateVehicleRequest() {

        VehicleRequest dto = VehicleRequest.builder()
                .brand("Toyota")
                .model("Fortuner")
                .vinNumber("VIN123")
                .category(VehicleCategory.SUV)
                .price(new BigDecimal("4500000"))
                .quantity(5)
                .condition(VehicleCondition.NEW)
                .status(VehicleStatus.AVAILABLE)
                .build();

        assertThat(validator.validate(dto)).isEmpty();

    }

}
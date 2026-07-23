package com.dealership.dto.response;

import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleResponseTest {

    @Test
    void shouldCreateVehicleResponse() {

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .brand("Toyota")
                .model("Fortuner")
                .category(VehicleCategory.SUV)
                .price(new BigDecimal("4500000"))
                .status(VehicleStatus.AVAILABLE)
                .build();

        assertThat(response.getBrand()).isEqualTo("Toyota");
        assertThat(response.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }
}

package com.dealership.dto.response;

import com.dealership.enums.InventoryOperation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryResponseTest {

    @Test
    void shouldCreateInventoryResponse() {

        LocalDateTime now = LocalDateTime.now();

        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .vehicleId(101L)
                .brand("Hyundai")
                .model("Creta")
                .vinNumber("VIN123456789")
                .operationType(InventoryOperation.ADD)
                .quantity(5)
                .availableQuantity(15)
                .date(now)
                .performedById(2L)
                .performedByName("Admin")
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVehicleId()).isEqualTo(101L);
        assertThat(response.getBrand()).isEqualTo("Hyundai");
        assertThat(response.getModel()).isEqualTo("Creta");
        assertThat(response.getVinNumber()).isEqualTo("VIN123456789");
        assertThat(response.getOperationType()).isEqualTo(InventoryOperation.ADD);
        assertThat(response.getQuantity()).isEqualTo(5);
        assertThat(response.getAvailableQuantity()).isEqualTo(15);
        assertThat(response.getDate()).isEqualTo(now);
        assertThat(response.getPerformedById()).isEqualTo(2L);
        assertThat(response.getPerformedByName()).isEqualTo("Admin");
    }
}
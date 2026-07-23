package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class VehicleImageResponseTest {


    @Test
    void shouldCreateVehicleImageResponse(){

        VehicleImageResponse response =
                VehicleImageResponse.builder()
                .id(1L)
                .vehicleId(10L)
                .imageUrl("car.jpg")
                .build();


        assertThat(response.getImageUrl())
                .isEqualTo("car.jpg");
    }
}
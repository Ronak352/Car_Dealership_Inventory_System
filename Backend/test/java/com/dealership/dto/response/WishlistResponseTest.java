package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistResponseTest {

    @Test
    void shouldCreateWishlistResponse(){

        WishlistResponse response =
                WishlistResponse.builder()
                .id(1L)
                .customerName("Ronak")
                .vehicleName("Mahindra XUV700")
                .build();


        assertThat(response.getVehicleName())
                .isEqualTo("Mahindra XUV700");
    }
}
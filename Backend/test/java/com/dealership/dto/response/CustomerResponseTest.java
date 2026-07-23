package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerResponseTest {

    @Test
    void shouldCreateCustomerResponse() {

        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .fullName("Ronak Rathod")
                .email("ronak@gmail.com")
                .phone("9876543210")
                .address("Ahmedabad")
                .build();

        assertThat(response.getFullName()).isEqualTo("Ronak Rathod");
        assertThat(response.getEmail()).contains("@");
    }
}

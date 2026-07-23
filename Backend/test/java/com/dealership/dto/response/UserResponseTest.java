package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void shouldCreateUserResponse() {

        UserResponse response = UserResponse.builder()
                .id(1L)
                .firstName("Ronak")
                .lastName("Rathod")
                .email("ronak@gmail.com")
                .phone("9876543210")
                .role("ADMIN")
                .status("ACTIVE")
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Ronak");
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }
}

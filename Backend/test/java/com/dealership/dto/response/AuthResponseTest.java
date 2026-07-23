
package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void shouldCreateAuthResponse(){

        AuthResponse response =
                AuthResponse.builder()
                .token("jwt-token")
                .userId(1L)
                .email("ronak@gmail.com")
                .role("ADMIN")
                .build();


        assertThat(response.getToken())
                .isEqualTo("jwt-token");

        assertThat(response.getRole())
                .isEqualTo("ADMIN");
    }
}

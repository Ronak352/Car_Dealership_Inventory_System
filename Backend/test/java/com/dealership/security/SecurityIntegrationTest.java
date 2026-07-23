package com.dealership.security;


import com.dealership.controller.VehicleController;

import com.dealership.dto.response.VehicleResponse;

import com.dealership.repository.UserRepository;

import com.dealership.service.VehicleService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * End-to-end test of the JWT security pipeline for a single representative
 * endpoint: {@link JwtAuthenticationFilter} -> {@link SecurityConfig} ->
 * {@link JwtAuthenticationEntryPoint} / {@link CustomAccessDeniedHandler}.
 *
 * Unlike {@link RoleAuthorizationTest}, authentication here is NOT
 * shortcut with {@code @WithMockUser}: real bearer tokens (or the lack of
 * one) are sent on the request so the whole authentication path is
 * exercised, including error response shape.
 */
@WebMvcTest(controllers = VehicleController.class)
@Import({
        SecurityConfig.class,
        JwtUtil.class,
        JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class SecurityIntegrationTest {

    private static final String SECRET =
            "RonakCarDealershipJWTSecretKey2026@Secure#Inventory$System";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserRepository userRepository;


    @Test
    void missingTokenIsRejectedWithUnauthorized() throws Exception {

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication token is missing"));
    }


    @Test
    void invalidTokenIsRejectedWithUnauthorized() throws Exception {

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication token is invalid"));
    }


    @Test
    void expiredTokenIsRejectedWithUnauthorized() throws Exception {

        JwtUtil alreadyExpiredIssuer = new JwtUtil(SECRET, -1000L);

        String expiredToken =
                alreadyExpiredIssuer.generateToken("jane@gmail.com", "CUSTOMER");

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication token has expired"));
    }


    @Test
    void validTokenForKnownUserIsAuthenticated() throws Exception {

        String token = jwtUtil.generateToken("jane@gmail.com", "CUSTOMER");

        when(customUserDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        org.springframework.security.core.userdetails.User
                                .builder()
                                .username("jane@gmail.com")
                                .password("irrelevant")
                                .roles("CUSTOMER")
                                .build()
                );

        when(vehicleService.getAvailableVehicles())
                .thenReturn(Collections.singletonList(VehicleResponse.builder().build()));

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

package com.dealership.controller;


import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;

import com.dealership.security.CustomUserDetailsService;
import com.dealership.security.JwtUtil;

import com.dealership.service.AuthService;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {



    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private AuthService authService;



    // Required because JWT security beans are loaded

    @MockBean
    private JwtUtil jwtUtil;



    @MockBean
    private CustomUserDetailsService customUserDetailsService;





    // ===============================
    // REGISTER TEST
    // ===============================


    @Test
    void shouldRegisterUser() throws Exception {



        AuthResponse response =
                AuthResponse.builder()

                        .userId(1L)

                        .email("john@gmail.com")

                        .role("CUSTOMER")

                        .build();




        when(authService.register(any(RegisterRequest.class)))

                .thenReturn(response);





        mockMvc.perform(

                post("/api/auth/register")

                .contentType(MediaType.APPLICATION_JSON)

                .content("""
                {
                    "firstName":"John",
                    "lastName":"Smith",
                    "email":"john@gmail.com",
                    "phone":"9999999999",
                    "password":"password123",
                    "role":"CUSTOMER"
                }
                """)

        )

        .andExpect(status().isCreated());


    }






    // ===============================
    // LOGIN TEST
    // ===============================


    @Test
    void shouldLoginUser() throws Exception {



        AuthResponse response =
                AuthResponse.builder()

                        .userId(1L)

                        .email("john@gmail.com")

                        .role("CUSTOMER")

                        .token("jwt-token")

                        .build();




        when(authService.login(any(LoginRequest.class)))

                .thenReturn(response);





        mockMvc.perform(

                post("/api/auth/login")

                .contentType(MediaType.APPLICATION_JSON)

                .content("""
                {
                    "email":"john@gmail.com",
                    "password":"password123"
                }
                """)

        )

        .andExpect(status().isOk());


    }


}
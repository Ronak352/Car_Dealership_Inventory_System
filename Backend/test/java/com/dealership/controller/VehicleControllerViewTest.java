package com.dealership.controller;


import com.dealership.dto.response.VehicleResponse;
import com.dealership.security.CustomUserDetailsService;
import com.dealership.security.JwtUtil;
import com.dealership.service.VehicleService;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.List;


import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleControllerViewTest {



    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private VehicleService vehicleService;



    // Security dependencies
    @MockBean
    private JwtUtil jwtUtil;


    @MockBean
    private CustomUserDetailsService customUserDetailsService;





    @Test
    void availableVehiclesShouldBeReturned() throws Exception {



        VehicleResponse response =

                VehicleResponse.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .price(new BigDecimal("4500000"))

                .quantity(5)

                .build();



        when(
                vehicleService.getAvailableVehicles()
        )

        .thenReturn(
                List.of(response)
        );




        mockMvc.perform(

                get("/api/vehicles")

        )

        .andExpect(status().isOk())

        .andExpect(
                jsonPath("$[0].brand")
                .value("Toyota")
        )

        .andExpect(
                jsonPath("$[0].model")
                .value("Fortuner")
        );



    }



}
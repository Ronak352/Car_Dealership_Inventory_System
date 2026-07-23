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

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;





@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleUpdateControllerTest {



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
    void vehicleShouldBeUpdatedSuccessfully() throws Exception {



        VehicleResponse response =

                VehicleResponse.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .price(
                        new BigDecimal("4500000")
                )

                .quantity(5)

                .build();





        when(

                vehicleService.updateVehicle(
                        eq(1L),
                        any()
                )

        )

        .thenReturn(response);






        mockMvc.perform(

                put("/api/vehicles/1")

                .contentType(MediaType.APPLICATION_JSON)

                .content("""
                {
                    "brand":"Toyota",
                    "model":"Fortuner",
                    "variant":"Legender",
                    "category":"SUV",
                    "fuelType":"DIESEL",
                    "transmission":"AUTOMATIC",
                    "manufacturingYear":2026,
                    "color":"Black",
                    "engineNumber":"ENG12345",
                    "vinNumber":"VIN123456",
                    "price":4500000,
                    "discount":100000,
                    "quantity":5,
                    "condition":"NEW",
                    "status":"AVAILABLE"
                }
                """)

        )


        .andExpect(status().isOk());



    }



}
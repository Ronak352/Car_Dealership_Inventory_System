package com.dealership.controller;


import com.dealership.security.CustomUserDetailsService;
import com.dealership.security.JwtUtil;
import com.dealership.service.VehicleService;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleDeleteControllerTest {



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
    void vehicleShouldBeDeletedSuccessfully() throws Exception {



        doNothing()
                .when(vehicleService)
                .deleteVehicle(1L);




        mockMvc.perform(

                delete("/api/vehicles/1")

        )

        .andExpect(
                status().isNoContent()
        );




        verify(vehicleService)
                .deleteVehicle(1L);


    }



}
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


import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleSearchControllerTest {



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
    void searchVehicleByBrandShouldReturnVehicles() throws Exception {



        VehicleResponse response =

                VehicleResponse.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .price(
                        new BigDecimal("4500000")
                )

                .build();





        when(
                vehicleService.searchVehicles(
                        eq("Toyota"),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull()
                )
        )

        .thenReturn(
                List.of(response)
        );







        mockMvc.perform(

                get("/api/vehicles/search")
                
                .param("brand","Toyota")

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
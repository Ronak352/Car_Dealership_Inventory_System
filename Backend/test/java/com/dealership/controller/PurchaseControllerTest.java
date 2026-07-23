package com.dealership.controller;


import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.PurchaseResponse;
import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import com.dealership.service.PurchaseService;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = PurchaseController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.dealership.security.JwtAuthenticationFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest {



    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private PurchaseService purchaseService;




    // ======================================
    // CREATE PURCHASE TEST
    // ======================================


    @Test
    void shouldCreatePurchase() throws Exception {



        PurchaseResponse response =
                PurchaseResponse.builder()

                .id(1L)

                .customerName("John Smith")

                .vehicleName("Toyota Fortuner")

                .sellingPrice(
                        new BigDecimal("4000000")
                )

                .paymentMethod(
                        PaymentMethod.CASH
                )

                .purchaseStatus(
                        PurchaseStatus.BOOKED
                )

                .purchaseDate(
                        LocalDate.now()
                )

                .build();



        when(
                purchaseService.createPurchase(
                        any(PurchaseRequest.class)
                )
        )

        .thenReturn(response);




        mockMvc.perform(

                post("/api/purchases")

                .contentType(
                        MediaType.APPLICATION_JSON
                )

                .content("""
                {
                    "customerId":1,
                    "vehicleId":1,
                    "purchaseDate":"2026-07-22",
                    "sellingPrice":4000000,
                    "paymentMethod":"CASH",
                    "purchaseStatus":"BOOKED"
                }
                """)

        )

        .andExpect(
                status().isCreated()
        );

    }





    // ======================================
    // GET PURCHASE BY ID
    // ======================================


    @Test
    void shouldGetPurchaseById() throws Exception {



        PurchaseResponse response =
                PurchaseResponse.builder()

                .id(1L)

                .customerName("John Smith")

                .vehicleName("Toyota Fortuner")

                .build();




        when(
                purchaseService.getPurchaseById(1L)
        )

        .thenReturn(response);




        mockMvc.perform(

                get("/api/purchases/1")

        )

        .andExpect(
                status().isOk()
        );

    }





    // ======================================
    // GET PURCHASES BY CUSTOMER
    // ======================================


    @Test
    void shouldGetPurchasesByCustomer() throws Exception {



        when(
                purchaseService.getPurchasesByCustomer(1L)
        )

        .thenReturn(
                List.of(
                        PurchaseResponse.builder()
                        .id(1L)
                        .build()
                )
        );




        mockMvc.perform(

                get("/api/purchases/customer/1")

        )

        .andExpect(
                status().isOk()
        );

    }





    // ======================================
    // GET PURCHASES BY VEHICLE
    // ======================================


    @Test
    void shouldGetPurchasesByVehicle() throws Exception {



        when(
                purchaseService.getPurchasesByVehicle(1L)
        )

        .thenReturn(
                List.of(
                        PurchaseResponse.builder()
                        .id(1L)
                        .build()
                )
        );




        mockMvc.perform(

                get("/api/purchases/vehicle/1")

        )

        .andExpect(
                status().isOk()
        );

    }





    // ======================================
    // GET PURCHASES BY SALESPERSON
    // ======================================


    @Test
    void shouldGetPurchasesBySalesperson() throws Exception {



        when(
                purchaseService.getPurchasesBySalesperson(1L)
        )

        .thenReturn(
                List.of(
                        PurchaseResponse.builder()
                        .id(1L)
                        .build()
                )
        );




        mockMvc.perform(

                get("/api/purchases/salesperson/1")

        )

        .andExpect(
                status().isOk()
        );

    }


}
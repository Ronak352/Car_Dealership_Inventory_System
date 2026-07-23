package com.dealership.controller;


import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.response.CustomerResponse;
import com.dealership.service.CustomerService;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = CustomerController.class,
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
class CustomerControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private CustomerService customerService;


    @Autowired
    private ObjectMapper objectMapper;




    @Test
    void createCustomerTest() throws Exception {


        CustomerRequest request =
                CustomerRequest.builder()
                .address("SG Highway")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();



        CustomerResponse response =
                CustomerResponse.builder()
                .id(1L)
                .fullName("Ronak Rathod")
                .email("ronak@gmail.com")
                .phone("9999999999")
                .address("SG Highway")
                .build();



        when(customerService.createCustomer(
                eq(1L),
                any(CustomerRequest.class)))
                .thenReturn(response);



        mockMvc.perform(
                post("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )

        .andExpect(status().isCreated())

        .andExpect(
                jsonPath("$.id")
                .value(1)
        )

        .andExpect(
                jsonPath("$.email")
                .value("ronak@gmail.com")
        );

    }





    @Test
    void getCustomerByIdTest() throws Exception {


        CustomerResponse response =
                CustomerResponse.builder()
                .id(1L)
                .fullName("Ronak Rathod")
                .email("ronak@gmail.com")
                .build();



        when(customerService.getCustomerById(1L))
                .thenReturn(response);



        mockMvc.perform(
                get("/api/customers/1")
        )

        .andExpect(status().isOk())

        .andExpect(
                jsonPath("$.id")
                .value(1)
        );

    }





    @Test
    void getCustomerByUserIdTest() throws Exception {


        CustomerResponse response =
                CustomerResponse.builder()
                .id(1L)
                .fullName("Ronak Rathod")
                .build();



        when(customerService.getCustomerByUserId(5L))
                .thenReturn(response);



        mockMvc.perform(
                get("/api/customers/user/5")
        )

        .andExpect(status().isOk())

        .andExpect(
                jsonPath("$.id")
                .value(1)
        );

    }





    @Test
    void getAllCustomersTest() throws Exception {


        when(customerService.getAllCustomers())

        .thenReturn(

            List.of(
                CustomerResponse.builder()
                .id(1L)
                .fullName("Ronak Rathod")
                .build()
            )

        );



        mockMvc.perform(
                get("/api/customers")
        )

        .andExpect(status().isOk())

        .andExpect(
                jsonPath("$.size()")
                .value(1)
        );

    }





    @Test
    void updateCustomerTest() throws Exception {


        CustomerRequest request =
                CustomerRequest.builder()
                .address("Updated Address")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();



        CustomerResponse response =
                CustomerResponse.builder()
                .id(1L)
                .address("Updated Address")
                .build();



        when(customerService.updateCustomer(
                eq(1L),
                any(CustomerRequest.class)))
                .thenReturn(response);



        mockMvc.perform(
                put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )

        .andExpect(status().isOk())

        .andExpect(
                jsonPath("$.address")
                .value("Updated Address")
        );

    }





    @Test
    void deleteCustomerTest() throws Exception {


        mockMvc.perform(
                delete("/api/customers/1")
        )

        .andExpect(status().isOk());

    }


}
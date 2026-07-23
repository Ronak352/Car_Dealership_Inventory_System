package com.dealership.controller;


import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.enums.Role;
import com.dealership.service.EmployeeService;

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


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(
        controllers = EmployeeController.class,
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
class EmployeeControllerTest {




    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private ObjectMapper objectMapper;



    @MockBean
    private EmployeeService employeeService;





    @Test
    void shouldCreateEmployee() throws Exception {


        EmployeeRequest request =
                EmployeeRequest.builder()
                        .userId(1L)
                        .employeeCode("EMP001")
                        .joiningDate(LocalDate.now())
                        .salary(BigDecimal.valueOf(50000))
                        .role(Role.SALESPERSON)
                        .build();



        EmployeeResponse response =
                EmployeeResponse.builder()
                        .id(1L)
                        .employeeCode("EMP001")
                        .role("SALESPERSON")
                        .build();



        when(employeeService.createEmployee(any(EmployeeRequest.class)))
                .thenReturn(response);



        mockMvc.perform(
                post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.employeeCode")
                .value("EMP001"));



        verify(employeeService)
                .createEmployee(any(EmployeeRequest.class));

    }







    @Test
    void shouldGetEmployeeById() throws Exception {



        EmployeeResponse response =
                EmployeeResponse.builder()
                        .id(1L)
                        .employeeCode("EMP001")
                        .role("MANAGER")
                        .build();



        when(employeeService.getEmployeeById(1L))
                .thenReturn(response);



        mockMvc.perform(
                get("/api/employees/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.employeeCode")
                .value("EMP001"));



    }







    @Test
    void shouldGetAllEmployees() throws Exception {


        when(employeeService.getAllEmployees())
                .thenReturn(
                        List.of(
                                EmployeeResponse.builder()
                                        .id(1L)
                                        .employeeCode("EMP001")
                                        .build()
                        )
                );



        mockMvc.perform(
                get("/api/employees")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()")
                .value(1));


    }







    @Test
    void shouldSearchEmployees() throws Exception {


        when(employeeService.searchEmployees("John"))
                .thenReturn(
                        List.of(
                                EmployeeResponse.builder()
                                        .employeeCode("EMP001")
                                        .build()
                        )
                );



        mockMvc.perform(
                get("/api/employees/search")
                        .param("keyword","John")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()")
                .value(1));

    }







    @Test
    void shouldDeleteEmployee() throws Exception {



        doNothing()
                .when(employeeService)
                .deleteEmployee(1L);



        mockMvc.perform(
                delete("/api/employees/1")
        )
        .andExpect(status().isOk())
        .andExpect(
                content().string(
                        "Employee deleted successfully"
                )
        );



        verify(employeeService)
                .deleteEmployee(1L);

    }







    @Test
    void shouldAssignRole() throws Exception {



        EmployeeResponse response =
                EmployeeResponse.builder()
                        .id(1L)
                        .role("MANAGER")
                        .build();



        when(employeeService.assignRole(
                1L,
                Role.MANAGER
        ))
        .thenReturn(response);



        mockMvc.perform(
                put("/api/employees/1/role")
                        .param(
                                "role",
                                "MANAGER"
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role")
                .value("MANAGER"));



    }



}
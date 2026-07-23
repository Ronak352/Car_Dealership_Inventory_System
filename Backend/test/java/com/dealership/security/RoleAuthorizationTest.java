package com.dealership.security;


import com.dealership.controller.EmployeeController;
import com.dealership.controller.TestDriveController;
import com.dealership.controller.VehicleController;

import com.dealership.dto.response.EmployeeResponse;
import com.dealership.dto.response.TestDriveBookingResponse;
import com.dealership.dto.response.VehicleResponse;

import com.dealership.repository.UserRepository;

import com.dealership.service.EmployeeService;
import com.dealership.service.TestDriveService;
import com.dealership.service.VehicleService;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * Verifies controller role authorization rules.
 */
@WebMvcTest(controllers = {
        EmployeeController.class,
        VehicleController.class,
        TestDriveController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class RoleAuthorizationTest {


    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private EmployeeService employeeService;


    @MockBean
    private VehicleService vehicleService;


    @MockBean
    private TestDriveService testDriveService;


    @MockBean
    private JwtUtil jwtUtil;


    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @MockBean
    private UserRepository userRepository;



    // ==========================================
    // EMPLOYEE DELETE - ADMIN ONLY
    // ==========================================


    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteEmployee() throws Exception {


        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk());

    }



    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotDeleteEmployee() throws Exception {


        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());

    }



    @Test
    @WithMockUser(roles = "SALESPERSON")
    void salesPersonCannotDeleteEmployee() throws Exception {


        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());

    }



    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotDeleteEmployee() throws Exception {


        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());

    }





    // ==========================================
    // EMPLOYEE LIST - ADMIN/MANAGER
    // ==========================================


    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanViewAllEmployees() throws Exception {


        when(employeeService.getAllEmployees())
                .thenReturn(
                        List.of(EmployeeResponse.builder().build())
                );


        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());

    }




    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotViewAllEmployees() throws Exception {


        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isForbidden());

    }





    // ==========================================
    // VEHICLE VIEW
    // ==========================================


    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanViewAvailableVehicles() throws Exception {


        when(vehicleService.getAvailableVehicles())
                .thenReturn(
                        Collections.singletonList(
                                VehicleResponse.builder().build()
                        )
                );


        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());

    }





    @Test
    @WithMockUser(roles = "SALESPERSON")
    void salesPersonCanViewAvailableVehicles() throws Exception {


        when(vehicleService.getAvailableVehicles())
                .thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());

    }





    // ==========================================
    // ADD VEHICLE - ADMIN/MANAGER ONLY
    // ==========================================


    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotAddVehicle() throws Exception {


        String vehicleJson = """
                {
                    "brand":"Toyota",
                    "model":"Fortuner",
                    "category":"SUV",
                    "vinNumber":"VIN123456789",
                    "price":2500000,
                    "condition":"NEW",
                    "status":"AVAILABLE"
                }
                """;


        mockMvc.perform(post("/api/vehicles")
                        .contentType("application/json")
                        .content(vehicleJson))
                .andExpect(status().isForbidden());

    }





    // ==========================================
    // TEST DRIVE ASSIGN SALESPERSON
    // ADMIN/MANAGER ONLY
    // ==========================================


    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanAssignSalesperson() throws Exception {


        when(testDriveService.assignSalesperson(1L,2L))
                .thenReturn(
                        TestDriveBookingResponse.builder().build()
                );


        mockMvc.perform(
                put("/api/test-drives/1/assign/2")
        )
        .andExpect(status().isOk());

    }





    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotAssignSalesperson() throws Exception {


        mockMvc.perform(
                put("/api/test-drives/1/assign/2")
        )
        .andExpect(status().isForbidden());

    }





    @Test
    @WithMockUser(roles = "SALESPERSON")
    void salesPersonCannotAssignSalesperson() throws Exception {


        mockMvc.perform(
                put("/api/test-drives/1/assign/2")
        )
        .andExpect(status().isForbidden());

    }

}
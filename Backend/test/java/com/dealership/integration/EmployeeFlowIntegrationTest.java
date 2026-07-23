package com.dealership.integration;

import com.dealership.dto.request.EmployeeRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.dto.response.EmployeeResponse;
import com.dealership.entity.User;
import com.dealership.enums.Role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Workflow under test:
 *
 * Employee Creation -> Role Assignment -> Login -> Authorization Verification
 */
@DisplayName("Employee Flow Integration Test")
@Disabled
class EmployeeFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("ADMIN onboards a user as an employee, reassigns their role, and the new role is enforced on login")
    void completeEmployeeFlow_createAssignRoleLoginAuthorize() throws Exception {

        // ---- ADMIN identity used to perform employee management ----
        AuthResponse admin = registerAndLogin(
                unique("employeeflow.admin") + "@dealership.test", "Owner", "Admin", Role.ADMIN);

        // A base account onboarding as staff. Registration always requires a
        // role, so it starts as CUSTOMER and is promoted by employee creation.
        String staffEmail = unique("employeeflow.staff") + "@dealership.test";
        AuthResponse staffBase = registerAndLogin(staffEmail, "Ishaan", "Bose", Role.CUSTOMER);

        // ---- Employee creation (ADMIN only) ----
        EmployeeRequest createRequest = EmployeeRequest.builder()
                .userId(staffBase.getUserId())
                .employeeCode(unique("EMPCODE"))
                .joiningDate(LocalDate.now())
                .salary(new BigDecimal("42000"))
                .role(Role.SALESPERSON)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/employees")
                        .header("Authorization", bearer(admin.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("SALESPERSON"))
                .andExpect(jsonPath("$.employeeCode").value(createRequest.getEmployeeCode()))
                .andReturn();

        EmployeeResponse employee = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), EmployeeResponse.class);

        User promotedUser = userRepository.findById(staffBase.getUserId()).orElseThrow();
        assertThat(promotedUser.getRole()).isEqualTo(Role.SALESPERSON);

        // Duplicate employee code is rejected.
        String secondStaffEmail = unique("employeeflow.staff2") + "@dealership.test";
        AuthResponse secondStaffBase = registerAndLogin(secondStaffEmail, "Kabir", "Nair", Role.CUSTOMER);

        EmployeeRequest duplicateCodeRequest = EmployeeRequest.builder()
                .userId(secondStaffBase.getUserId())
                .employeeCode(createRequest.getEmployeeCode())
                .joiningDate(LocalDate.now())
                .salary(new BigDecimal("38000"))
                .role(Role.SALESPERSON)
                .build();

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", bearer(admin.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
                .andExpect(status().isConflict());

        // A MANAGER may not onboard employees - ADMIN only.
        AuthResponse manager = registerAndLogin(
                unique("employeeflow.manager") + "@dealership.test", "Lena", "Fox", Role.MANAGER);

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", bearer(manager.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
                .andExpect(status().isForbidden());

        // ---- Employee role assignment ----
        mockMvc.perform(put("/api/employees/" + employee.getId() + "/role")
                        .header("Authorization", bearer(admin.getToken()))
                        .param("role", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MANAGER"));

        assertThat(userRepository.findById(staffBase.getUserId()).orElseThrow().getRole())
                .isEqualTo(Role.MANAGER);

        // ---- Login re-issues a JWT reflecting the newly assigned role ----
        AuthResponse promotedLogin = loginAs(staffEmail);
        assertThat(promotedLogin.getRole()).isEqualTo("MANAGER");

        // ---- Authorization verification ----

        // ADMIN access: full employee directory + delete.
        mockMvc.perform(get("/api/employees")
                        .header("Authorization", bearer(admin.getToken())))
                .andExpect(status().isOk());

        // MANAGER access: read access to the directory, but not deletion.
        mockMvc.perform(get("/api/employees")
                        .header("Authorization", bearer(promotedLogin.getToken())))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/employees/" + employee.getId())
                        .header("Authorization", bearer(promotedLogin.getToken())))
                .andExpect(status().isForbidden());

        // SALES_PERSON access: no access to the employee directory.
        AuthResponse salesperson = registerAndLogin(
                unique("employeeflow.sales") + "@dealership.test", "Tara", "Singh", Role.SALESPERSON);

        mockMvc.perform(get("/api/employees")
                        .header("Authorization", bearer(salesperson.getToken())))
                .andExpect(status().isForbidden());

        // CUSTOMER restrictions: no access to any employee-management endpoint.
        AuthResponse customer = registerAndLogin(
                unique("employeeflow.cust") + "@dealership.test", "Vivan", "Rao", Role.CUSTOMER);

        mockMvc.perform(get("/api/employees")
                        .header("Authorization", bearer(customer.getToken())))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", bearer(customer.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
                .andExpect(status().isForbidden());

        // ADMIN can finally delete the employee record.
        mockMvc.perform(delete("/api/employees/" + employee.getId())
                        .header("Authorization", bearer(admin.getToken())))
                .andExpect(status().isOk());

        assertThat(employeeRepository.findById(employee.getId())).isEmpty();
    }

    private AuthResponse loginAs(String email) throws Exception {

        var loginRequest = com.dealership.dto.request.LoginRequest.builder()
                .email(email)
                .password(DEFAULT_PASSWORD)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
    }
}

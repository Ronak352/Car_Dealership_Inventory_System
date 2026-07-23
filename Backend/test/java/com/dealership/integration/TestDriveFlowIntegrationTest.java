package com.dealership.integration;

import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.dto.response.CustomerResponse;
import com.dealership.entity.Employee;
import com.dealership.entity.TestDriveBooking;
import com.dealership.entity.Vehicle;
import com.dealership.enums.Role;
import com.dealership.enums.TestDriveStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
/**
 * Workflow under test:
 *
 * Customer -> Test Drive Booking -> Salesperson Assignment -> Approval ->
 * Status Update (approve / reject / complete)
 */
@DisplayName("Test Drive Flow Integration Test")
@Disabled

class TestDriveFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("A customer can book a test drive, a manager assigns a salesperson, and the booking is approved and completed")
    void completeTestDriveFlow_bookAssignApproveComplete() throws Exception {

        // ---- Customer booking creation ----
        String customerEmail = unique("testdrive.customer") + "@dealership.test";
        AuthResponse customerAuth = registerAndLogin(customerEmail, "Neha", "Verma", Role.CUSTOMER);

        CustomerResponse customer = createCustomerProfile(customerAuth);

        Vehicle vehicle = createVehicle("Hyundai", "Creta", 5, new BigDecimal("1550000"));

        TestDriveBookingRequest bookingRequest = TestDriveBookingRequest.builder()
                .customerId(customer.getId())
                .vehicleId(vehicle.getId())
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(2))
                .build();

        MvcResult bookingResult = mockMvc.perform(post("/api/test-drives")
                        .header("Authorization", bearer(customerAuth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.vehicleName").value("Hyundai Creta"))
                .andReturn();

        Long bookingId = testDriveRepository
                .findByCustomerId(customer.getId())
                .get(0)
                .getId();

        // ---- Salesperson assignment (ADMIN/MANAGER only) ----
        String managerEmail = unique("testdrive.manager") + "@dealership.test";
        AuthResponse managerAuth = registerAndLogin(managerEmail, "Ravi", "Kapoor", Role.MANAGER);

        Employee salesperson = createSalesperson("Amit");

        mockMvc.perform(put("/api/test-drives/" + bookingId + "/assign/" + salesperson.getId())
                        .header("Authorization", bearer(managerAuth.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salespersonName").value("Amit Salesperson"));

        // ---- Approval process ----
        mockMvc.perform(put("/api/test-drives/" + bookingId + "/status")
                        .header("Authorization", bearer(managerAuth.getToken()))
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        TestDriveBooking approvedBooking = testDriveRepository.findById(bookingId).orElseThrow();
        assertThat(approvedBooking.getStatus()).isEqualTo(TestDriveStatus.APPROVED);
        assertThat(approvedBooking.getSalesperson().getId()).isEqualTo(salesperson.getId());

        // ---- Status transition through to completion ----
        mockMvc.perform(put("/api/test-drives/" + bookingId + "/status")
                        .header("Authorization", bearer(managerAuth.getToken()))
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        assertThat(testDriveRepository.findById(bookingId).orElseThrow().getStatus())
                .isEqualTo(TestDriveStatus.COMPLETED);
    }

    @Test
    @DisplayName("A booking request against a non-existent vehicle is rejected")
    void bookingAgainstUnknownVehicle_returnsNotFound() throws Exception {

        String customerEmail = unique("testdrive.badvehicle") + "@dealership.test";
        AuthResponse customerAuth = registerAndLogin(customerEmail, "Karan", "Joshi", Role.CUSTOMER);

        CustomerResponse customer = createCustomerProfile(customerAuth);

        TestDriveBookingRequest bookingRequest = TestDriveBookingRequest.builder()
                .customerId(customer.getId())
                .vehicleId(999_999L)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(post("/api/test-drives")
                        .header("Authorization", bearer(customerAuth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A booking can be rejected (cancelled) instead of approved")
    void rejectProcess_movesBookingToCancelled() throws Exception {

        String customerEmail = unique("testdrive.reject") + "@dealership.test";
        AuthResponse customerAuth = registerAndLogin(customerEmail, "Sara", "Khan", Role.CUSTOMER);
        CustomerResponse customer = createCustomerProfile(customerAuth);

        Vehicle vehicle = createVehicle("Kia", "Seltos", 4, new BigDecimal("1400000"));

        TestDriveBookingRequest bookingRequest = TestDriveBookingRequest.builder()
                .customerId(customer.getId())
                .vehicleId(vehicle.getId())
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(3))
                .build();

        mockMvc.perform(post("/api/test-drives")
                        .header("Authorization", bearer(customerAuth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());

        Long bookingId = testDriveRepository.findByCustomerId(customer.getId()).get(0).getId();

        String salespersonEmail = unique("testdrive.rejectsp") + "@dealership.test";
        AuthResponse salespersonAuth = registerAndLogin(salespersonEmail, "Dev", "Rana", Role.SALESPERSON);

        mockMvc.perform(put("/api/test-drives/" + bookingId + "/status")
                        .header("Authorization", bearer(salespersonAuth.getToken()))
                        .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        assertThat(testDriveRepository.findById(bookingId).orElseThrow().getStatus())
                .isEqualTo(TestDriveStatus.CANCELLED);
    }

    @Test
    @DisplayName("Only ADMIN/MANAGER may assign a salesperson or delete a booking; a customer cannot")
    void permissions_customerCannotAssignSalespersonOrDeleteBooking() throws Exception {

        String customerEmail = unique("testdrive.perm") + "@dealership.test";
        AuthResponse customerAuth = registerAndLogin(customerEmail, "Meera", "Iyer", Role.CUSTOMER);
        CustomerResponse customer = createCustomerProfile(customerAuth);

        Vehicle vehicle = createVehicle("Mahindra", "XUV700", 2, new BigDecimal("2100000"));

        TestDriveBookingRequest bookingRequest = TestDriveBookingRequest.builder()
                .customerId(customer.getId())
                .vehicleId(vehicle.getId())
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(post("/api/test-drives")
                        .header("Authorization", bearer(customerAuth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());

        Long bookingId = testDriveRepository.findByCustomerId(customer.getId()).get(0).getId();

        Employee salesperson = createSalesperson("Rohit");

        mockMvc.perform(put("/api/test-drives/" + bookingId + "/assign/" + salesperson.getId())
                        .header("Authorization", bearer(customerAuth.getToken())))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/test-drives/" + bookingId)
                        .header("Authorization", bearer(customerAuth.getToken())))
                .andExpect(status().isForbidden());

        // CUSTOMER also cannot list every booking in the system.
        mockMvc.perform(get("/api/test-drives")
                        .header("Authorization", bearer(customerAuth.getToken())))
                .andExpect(status().isForbidden());
    }

    private CustomerResponse createCustomerProfile(AuthResponse auth) throws Exception {

        CustomerRequest customerRequest = CustomerRequest.builder()
                .address("42 Ring Road")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380015")
                .build();

        MvcResult result = mockMvc.perform(post("/api/customers/" + auth.getUserId())
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
    }
}

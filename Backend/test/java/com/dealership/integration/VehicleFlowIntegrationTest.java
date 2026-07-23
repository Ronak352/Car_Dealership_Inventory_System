package com.dealership.integration;

import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.entity.Vehicle;
import com.dealership.enums.FuelType;
import com.dealership.enums.Role;
import com.dealership.enums.Transmission;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Workflow under test:
 *
 * ADMIN/MANAGER add vehicles to the catalog -> any authenticated role can
 * browse/search it -> ADMIN/MANAGER update listings -> ADMIN alone can
 * delete them. A CUSTOMER is restricted to read-only access throughout.
 */

@DisplayName("Vehicle Flow Integration Test")
@Disabled
class VehicleFlowIntegrationTest extends AbstractIntegrationTest {

    private VehicleRequest sampleRequest(String vin) {

        return VehicleRequest.builder()
                .brand("Toyota")
                .model("Fortuner")
                .variant("Legender")
                .category(VehicleCategory.SUV)
                .fuelType(FuelType.DIESEL)
                .transmission(Transmission.AUTOMATIC)
                .manufacturingYear(2025)
                .color("Black")
                .engineNumber(unique("ENG"))
                .vinNumber(vin)
                .price(new BigDecimal("4200000"))
                .discount(BigDecimal.ZERO)
                .quantity(4)
                .condition(VehicleCondition.NEW)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    @DisplayName("ADMIN/MANAGER can add a vehicle; a CUSTOMER is forbidden from doing so")
    void adminAndManagerCanAddVehicle_customerCannot() throws Exception {

        AuthResponse admin = registerAndLogin(
                unique("vehicleflow.admin") + "@dealership.test", "Owner", "Admin", Role.ADMIN);

        VehicleRequest adminVehicle = sampleRequest(unique("VIN-ADMIN"));

        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", bearer(admin.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminVehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.vinNumber").value(adminVehicle.getVinNumber()))
                .andReturn();

        VehicleResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), VehicleResponse.class);

        assertThat(vehicleRepository.existsByVinNumber(adminVehicle.getVinNumber())).isTrue();

        AuthResponse manager = registerAndLogin(
                unique("vehicleflow.manager") + "@dealership.test", "Neel", "Shah", Role.MANAGER);

        VehicleRequest managerVehicle = sampleRequest(unique("VIN-MANAGER"));

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", bearer(manager.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(managerVehicle)))
                .andExpect(status().isCreated());

        AuthResponse customer = registerAndLogin(
                unique("vehicleflow.customer") + "@dealership.test", "Diya", "Sharma", Role.CUSTOMER);

        VehicleRequest customerAttempt = sampleRequest(unique("VIN-CUSTOMER"));

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", bearer(customer.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerAttempt)))
                .andExpect(status().isForbidden());

        assertThat(vehicleRepository.existsByVinNumber(customerAttempt.getVinNumber())).isFalse();

        // Any authenticated role, including the customer, can view/search the catalog.
        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", bearer(customer.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + created.getId() + ")]").exists());

        mockMvc.perform(get("/api/vehicles/search")
                        .header("Authorization", bearer(customer.getToken()))
                        .param("brand", "Toyota")
                        .param("category", "SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + created.getId() + ")]").exists());
    }

    @Test
    @DisplayName("Adding a vehicle with a VIN that already exists is rejected with a conflict")
    void addVehicle_duplicateVin_isRejectedWithConflict() throws Exception {

        AuthResponse admin = registerAndLogin(
                unique("vehicleflow.dupadmin") + "@dealership.test", "Kavya", "Rao", Role.ADMIN);

        VehicleRequest request = sampleRequest(unique("VIN-DUP"));

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", bearer(admin.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", bearer(admin.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("MANAGER can update a vehicle; a CUSTOMER is forbidden from updating it")
    void managerCanUpdateVehicle_customerCannot() throws Exception {

        Vehicle vehicle = createVehicle("Skoda", "Kushaq", 3, new BigDecimal("1450000"));

        AuthResponse manager = registerAndLogin(
                unique("vehicleflow.updatemanager") + "@dealership.test", "Aarav", "Joshi", Role.MANAGER);

        VehicleRequest updateRequest = sampleRequest(unique("VIN-UPDATED"));
        updateRequest.setBrand("Skoda");
        updateRequest.setModel("Kushaq Monte Carlo");
        updateRequest.setPrice(new BigDecimal("1550000"));

        mockMvc.perform(put("/api/vehicles/" + vehicle.getId())
                        .header("Authorization", bearer(manager.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Kushaq Monte Carlo"))
                .andExpect(jsonPath("$.price").value(1550000));

        assertThat(vehicleRepository.findById(vehicle.getId()).orElseThrow().getModel())
                .isEqualTo("Kushaq Monte Carlo");

        AuthResponse customer = registerAndLogin(
                unique("vehicleflow.updatecustomer") + "@dealership.test", "Ira", "Mehta", Role.CUSTOMER);

        mockMvc.perform(put("/api/vehicles/" + vehicle.getId())
                        .header("Authorization", bearer(customer.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Only ADMIN can delete a vehicle; MANAGER and CUSTOMER are forbidden")
    void onlyAdminCanDeleteVehicle() throws Exception {

        Vehicle vehicle = createVehicle("Renault", "Duster", 2, new BigDecimal("1350000"));

        AuthResponse manager = registerAndLogin(
                unique("vehicleflow.deletemanager") + "@dealership.test", "Yash", "Trivedi", Role.MANAGER);

        mockMvc.perform(delete("/api/vehicles/" + vehicle.getId())
                        .header("Authorization", bearer(manager.getToken())))
                .andExpect(status().isForbidden());

        AuthResponse customer = registerAndLogin(
                unique("vehicleflow.deletecustomer") + "@dealership.test", "Zara", "Khan", Role.CUSTOMER);

        mockMvc.perform(delete("/api/vehicles/" + vehicle.getId())
                        .header("Authorization", bearer(customer.getToken())))
                .andExpect(status().isForbidden());

        AuthResponse admin = registerAndLogin(
                unique("vehicleflow.deleteadmin") + "@dealership.test", "Vihaan", "Desai", Role.ADMIN);

        mockMvc.perform(delete("/api/vehicles/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken())))
                .andExpect(status().isNoContent());

        assertThat(vehicleRepository.findById(vehicle.getId())).isEmpty();
    }

    @Test
    @DisplayName("Browsing the vehicle catalog without a bearer token is rejected")
    void viewVehicles_withoutToken_isUnauthorized() throws Exception {

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }
}

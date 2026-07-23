package com.dealership.integration;

import com.dealership.dto.response.AuthResponse;
import com.dealership.entity.Vehicle;
import com.dealership.enums.Role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Workflow under test:
 *
 * ADMIN/MANAGER restock, sell down and set exact stock levels for a
 * vehicle, each operation is logged, and every role can read the current
 * available quantity while only ADMIN/MANAGER can see history/low-stock
 * reports or mutate stock at all.
 */
@DisplayName("Inventory Flow Integration Test")

@ Disabled
class InventoryFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("ADMIN can increase, decrease and set exact stock, and each change is logged")
    void adminCanIncreaseDecreaseAndUpdateStock() throws Exception {

        AuthResponse admin = registerAndLogin(
                unique("inventoryflow.admin") + "@dealership.test", "Kabir", "Malhotra", Role.ADMIN);

        Vehicle vehicle = createVehicle("Volkswagen", "Taigun", 5, new BigDecimal("1600000"));

        // ---- Increase stock ----
        mockMvc.perform(post("/api/inventory/increase/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken()))
                        .param("quantity", "3")
                        .param("performedByUserId", String.valueOf(admin.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType").value("ADD"))
                .andExpect(jsonPath("$.availableQuantity").value(8));

        assertThat(vehicleRepository.findById(vehicle.getId()).orElseThrow().getQuantity()).isEqualTo(8);

        // ---- Decrease stock ----
        mockMvc.perform(post("/api/inventory/decrease/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken()))
                        .param("quantity", "2")
                        .param("performedByUserId", String.valueOf(admin.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType").value("REMOVE"))
                .andExpect(jsonPath("$.availableQuantity").value(6));

        assertThat(vehicleRepository.findById(vehicle.getId()).orElseThrow().getQuantity()).isEqualTo(6);

        // ---- Set an exact quantity ----
        mockMvc.perform(put("/api/inventory/update/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken()))
                        .param("quantity", "10")
                        .param("performedByUserId", String.valueOf(admin.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType").value("UPDATE"))
                .andExpect(jsonPath("$.availableQuantity").value(10));

        assertThat(vehicleRepository.findById(vehicle.getId()).orElseThrow().getQuantity()).isEqualTo(10);

        // ---- History reflects all three movements, newest first ----
        mockMvc.perform(get("/api/inventory/history/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].operationType").value("UPDATE"));
    }

    @Test
    @DisplayName("Decreasing stock below zero is rejected with a bad request and leaves quantity unchanged")
    void decreaseStock_insufficientQuantity_returnsBadRequest() throws Exception {

        AuthResponse admin = registerAndLogin(
                unique("inventoryflow.insufficient") + "@dealership.test", "Reyansh", "Chopra", Role.ADMIN);

        Vehicle vehicle = createVehicle("Nissan", "Magnite", 2, new BigDecimal("950000"));

        mockMvc.perform(post("/api/inventory/decrease/" + vehicle.getId())
                        .header("Authorization", bearer(admin.getToken()))
                        .param("quantity", "5")
                        .param("performedByUserId", String.valueOf(admin.getUserId())))
                .andExpect(status().isBadRequest());

        assertThat(vehicleRepository.findById(vehicle.getId()).orElseThrow().getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Every authenticated role can read the available quantity, but only ADMIN/MANAGER can mutate stock or view reports")
    void rolesAreEnforcedForInventoryOperations() throws Exception {

        AuthResponse manager = registerAndLogin(
                unique("inventoryflow.manager") + "@dealership.test", "Anika", "Bhatt", Role.MANAGER);

        Vehicle vehicle = createVehicle("Mahindra", "Thar", 4, new BigDecimal("1600000"));

        AuthResponse salesperson = registerAndLogin(
                unique("inventoryflow.salesperson") + "@dealership.test", "Dhruv", "Menon", Role.SALESPERSON);

        AuthResponse customer = registerAndLogin(
                unique("inventoryflow.customer") + "@dealership.test", "Myra", "Iyer", Role.CUSTOMER);

        // MANAGER may mutate stock.
        mockMvc.perform(post("/api/inventory/increase/" + vehicle.getId())
                        .header("Authorization", bearer(manager.getToken()))
                        .param("quantity", "1")
                        .param("performedByUserId", String.valueOf(manager.getUserId())))
                .andExpect(status().isOk());

        // SALESPERSON and CUSTOMER may not mutate stock.
        mockMvc.perform(post("/api/inventory/increase/" + vehicle.getId())
                        .header("Authorization", bearer(salesperson.getToken()))
                        .param("quantity", "1")
                        .param("performedByUserId", String.valueOf(salesperson.getUserId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/inventory/increase/" + vehicle.getId())
                        .header("Authorization", bearer(customer.getToken()))
                        .param("quantity", "1")
                        .param("performedByUserId", String.valueOf(customer.getUserId())))
                .andExpect(status().isForbidden());

        // History and low-stock reports are restricted to ADMIN/MANAGER.
        mockMvc.perform(get("/api/inventory/history/" + vehicle.getId())
                        .header("Authorization", bearer(salesperson.getToken())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/inventory/low-stock")
                        .header("Authorization", bearer(customer.getToken())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/inventory/low-stock")
                        .header("Authorization", bearer(manager.getToken()))
                        .param("threshold", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + vehicle.getId() + ")]").exists());

        // But every authenticated role can read the plain available quantity.
        mockMvc.perform(get("/api/inventory/quantity/" + vehicle.getId())
                        .header("Authorization", bearer(salesperson.getToken())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inventory/quantity/" + vehicle.getId())
                        .header("Authorization", bearer(customer.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }
}

package com.dealership.integration;

import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.dto.response.CustomerResponse;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.dto.response.PurchaseResponse;
import com.dealership.entity.Vehicle;
import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import com.dealership.enums.PurchaseStatus;
import com.dealership.enums.Role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MvcResult;

import org.junit.jupiter.api.Disabled;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Workflow under test:
 *
 * Customer -> Vehicle Selection -> Purchase Creation -> Payment Processing
 * -> Purchase History Verification
 */
@DisplayName("Purchase Flow Integration Test")
@Disabled
class PurchaseFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("A logged-in customer can buy an available vehicle, pay for it, and see it in their purchase history")
    void completePurchaseFlow_createsPaymentAndReducesInventory() throws Exception {

        // ---- Customer authentication ----
        String email = unique("purchase.customer") + "@dealership.test";
        AuthResponse auth = registerAndLogin(email, "Priya", "Shah", Role.CUSTOMER);
        assertThat(auth.getToken()).isNotBlank();

        CustomerRequest customerRequest = CustomerRequest.builder()
                .address("221B Baker Street")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380001")
                .build();

        MvcResult customerResult = mockMvc.perform(post("/api/customers/" + auth.getUserId())
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse customer = objectMapper.readValue(
                customerResult.getResponse().getContentAsString(), CustomerResponse.class);

        // ---- Vehicle availability verification ----
        Vehicle vehicle = createVehicle("Honda", "City", 3, new BigDecimal("1250000"));

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", bearer(auth.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + vehicle.getId() + ")]").exists());

        // ---- Purchase creation ----
        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                .customerId(customer.getId())
                .vehicleId(vehicle.getId())
                .purchaseDate(LocalDate.now())
                .deliveryDate(LocalDate.now().plusDays(7))
                .sellingPrice(new BigDecimal("1225000"))
                .paymentMethod(PaymentMethod.CARD)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        MvcResult purchaseResult = mockMvc.perform(post("/api/purchases")
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleName").value("Honda City"))
                .andExpect(jsonPath("$.purchaseStatus").value("BOOKED"))
                .andReturn();

        PurchaseResponse purchase = objectMapper.readValue(
                purchaseResult.getResponse().getContentAsString(), PurchaseResponse.class);

        // ---- Inventory quantity update ----
        Vehicle refreshedVehicle = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertThat(refreshedVehicle.getQuantity()).isEqualTo(2);

        // ---- Payment creation ----
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .purchaseId(purchaseIdFrom(purchase))
                .amount(purchaseRequest.getSellingPrice())
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId(unique("TXN"))
                .paymentDate(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value(paymentRequest.getTransactionId()));

        mockMvc.perform(get("/api/payments/purchase/" + purchaseIdFrom(purchase))
                        .header("Authorization", bearer(auth.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(paymentRequest.getTransactionId()));

        // ---- Purchase history generation / verification ----
        mockMvc.perform(get("/api/purchases/customer/" + customer.getId())
                        .header("Authorization", bearer(auth.getToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehicleName").value("Honda City"))
                .andExpect(jsonPath("$[0].sellingPrice").value(1225000));

        assertThat(paymentRepository.findByPurchaseId(purchaseIdFrom(purchase))).hasSize(1);
    }

    @Test
    @DisplayName("Purchasing an out-of-stock vehicle does not create a purchase record or touch inventory")
    void purchaseOfOutOfStockVehicle_isRejected() throws Exception {

        String email = unique("purchase.reject") + "@dealership.test";
        AuthResponse auth = registerAndLogin(email, "Arjun", "Mehta", Role.CUSTOMER);

        CustomerRequest customerRequest = CustomerRequest.builder()
                .address("14 MG Road")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380009")
                .build();

        MvcResult customerResult = mockMvc.perform(post("/api/customers/" + auth.getUserId())
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse customer = objectMapper.readValue(
                customerResult.getResponse().getContentAsString(), CustomerResponse.class);

        Vehicle outOfStockVehicle = createVehicle("Tata", "Nexon", 0, new BigDecimal("900000"));

        long purchaseCountBefore = purchaseRepository.count();

        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                .customerId(customer.getId())
                .vehicleId(outOfStockVehicle.getId())
                .purchaseDate(LocalDate.now())
                .sellingPrice(new BigDecimal("890000"))
                .paymentMethod(PaymentMethod.CASH)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        mockMvc.perform(post("/api/purchases")
                        .header("Authorization", bearer(auth.getToken()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().is5xxServerError());

        assertThat(purchaseRepository.count()).isEqualTo(purchaseCountBefore);
    }

    /**
     * PurchaseResponse doesn't echo the persisted id as a first-class typed
     * field in a way callers can rely on without a lookup in production
     * code, but it is present on the DTO - this helper just makes the
     * dependency on it explicit and null-safe for the test.
     */
    private Long purchaseIdFrom(PurchaseResponse purchase) {
        assertThat(purchase.getId()).isNotNull();
        return purchase.getId();
    }
}

package com.dealership.controller;

import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import com.dealership.security.CustomUserDetailsService;
import com.dealership.security.JwtUtil;
import com.dealership.service.PaymentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // ==========================================
    // CREATE PAYMENT
    // ==========================================

    @Test
    void shouldCreatePayment() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("1500000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN1001")
                        .paymentDate(LocalDateTime.now())
                        .build();

        when(paymentService.createPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "purchaseId":1,
                            "amount":1500000,
                            "paymentMethod":"UPI",
                            "paymentStatus":"SUCCESS",
                            "transactionId":"TXN1001",
                            "paymentDate":"2026-01-10T10:30:00"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================

    @Test
    void shouldGetPaymentById() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("1500000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN1001")
                        .paymentDate(LocalDateTime.now())
                        .build();

        when(paymentService.getPaymentById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================

    @Test
    void shouldGetAllPayments() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("1500000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN1001")
                        .paymentDate(LocalDateTime.now())
                        .build();

        when(paymentService.getAllPayments())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // GET PAYMENTS BY PURCHASE
    // ==========================================

    @Test
    void shouldGetPaymentsByPurchase() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("1500000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN1001")
                        .paymentDate(LocalDateTime.now())
                        .build();

        when(paymentService.getPaymentsByPurchase(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/payments/purchase/1"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // UPDATE PAYMENT STATUS
    // ==========================================

    @Test
    void shouldUpdatePaymentStatus() throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("1500000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN1001")
                        .paymentDate(LocalDateTime.now())
                        .build();

        when(paymentService.updatePaymentStatus(
                eq(1L),
                eq(PaymentStatus.SUCCESS)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/payments/1/status")
                                .param("paymentStatus", "SUCCESS"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // DELETE PAYMENT
    // ==========================================

    @Test
    void shouldDeletePayment() throws Exception {

        doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isNoContent());
    }
}
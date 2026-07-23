package com.dealership.service;

import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    // ==========================================
    // CREATE PAYMENT
    // ==========================================
    PaymentResponse createPayment(PaymentRequest request);

    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================
    PaymentResponse getPaymentById(Long id);

    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================
    List<PaymentResponse> getAllPayments();

    // ==========================================
    // GET PAYMENTS BY PURCHASE
    // ==========================================
    List<PaymentResponse> getPaymentsByPurchase(Long purchaseId);

    // ==========================================
    // UPDATE PAYMENT STATUS
    // ==========================================
    PaymentResponse updatePaymentStatus(
            Long id,
            PaymentStatus paymentStatus
    );

    // ==========================================
    // DELETE PAYMENT
    // ==========================================
    void deletePayment(Long id);
}

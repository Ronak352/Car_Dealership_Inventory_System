package com.dealership.service.impl;

import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.entity.PaymentHistory;
import com.dealership.entity.PurchaseHistory;
import com.dealership.enums.PaymentStatus;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.PaymentRepository;
import com.dealership.repository.PurchaseRepository;
import com.dealership.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;

    // ==========================================
    // CREATE PAYMENT
    // ==========================================

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        if (paymentRepository.existsByTransactionId(request.getTransactionId())) {
            throw new DuplicateResourceException(
                    "Transaction ID already exists.");
        }

        PurchaseHistory purchase =
                purchaseRepository.findById(request.getPurchaseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase not found with id : "
                                                + request.getPurchaseId()));

        PaymentHistory payment =
                PaymentHistory.builder()
                        .purchase(purchase)
                        .amount(request.getAmount())
                        .paymentMethod(request.getPaymentMethod())
                        .paymentStatus(request.getPaymentStatus())
                        .transactionId(request.getTransactionId())
                        .paymentDate(request.getPaymentDate())
                        .build();

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================

    @Override
    public PaymentResponse getPaymentById(Long id) {

        PaymentHistory payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id : " + id));

        return mapToResponse(payment);
    }

    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================

    @Override
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    // ==========================================
    // GET PAYMENTS BY PURCHASE
    // ==========================================

    @Override
    public List<PaymentResponse> getPaymentsByPurchase(Long purchaseId) {

        purchaseRepository.findById(purchaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase not found with id : " + purchaseId));

        return paymentRepository
                .findByPurchaseId(purchaseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // UPDATE PAYMENT STATUS
    // ==========================================

    @Override
    public PaymentResponse updatePaymentStatus(
            Long id,
            PaymentStatus paymentStatus) {

        PaymentHistory payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id : " + id));

        payment.setPaymentStatus(paymentStatus);

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    // ==========================================
    // DELETE PAYMENT
    // ==========================================

    @Override
    public void deletePayment(Long id) {

        PaymentHistory payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id : " + id));

        paymentRepository.delete(payment);
    }
    
    // ==========================================
    // MAP ENTITY TO RESPONSE
    // ==========================================

    private PaymentResponse mapToResponse(PaymentHistory payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

}    
    
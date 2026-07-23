package com.dealership.controller;

import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.enums.PaymentStatus;
import com.dealership.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment records tied to a purchase, including status updates.")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    // ==========================================
    // CREATE PAYMENT
    // ==========================================

    @Operation(summary = "Record a payment", description = "Creates a payment against an existing purchase.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment recorded"),
            @ApiResponse(responseCode = "404", description = "Purchase not found")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALESPERSON','CUSTOMER')")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================

    @Operation(summary = "Get a payment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }

    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================

    @Operation(summary = "List all payments", description = "ADMIN only.")
    @ApiResponse(responseCode = "200", description = "All payments")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    // ==========================================
    // GET PAYMENTS BY PURCHASE
    // ==========================================

    @Operation(summary = "Get payments for a purchase")
    @ApiResponse(responseCode = "200", description = "Payments for this purchase (may be empty)")
    @GetMapping("/purchase/{purchaseId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByPurchase(
            @PathVariable Long purchaseId
    ) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByPurchase(purchaseId)
        );
    }

    // ==========================================
    // UPDATE PAYMENT STATUS
    // ==========================================

    @Operation(summary = "Update a payment's status", description = "ADMIN only. Transitions PENDING -> SUCCESS/FAILED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus paymentStatus
    ) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, paymentStatus)
        );
    }

    // ==========================================
    // DELETE PAYMENT
    // ==========================================

    @Operation(summary = "Delete a payment", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id
    ) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }

}
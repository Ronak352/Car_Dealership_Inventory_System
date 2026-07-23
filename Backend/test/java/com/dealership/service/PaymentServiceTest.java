package com.dealership.service;

import com.dealership.dto.request.PaymentRequest;
import com.dealership.dto.response.PaymentResponse;
import com.dealership.entity.Customer;
import com.dealership.entity.PaymentHistory;
import com.dealership.entity.PurchaseHistory;
import com.dealership.entity.User;
import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import com.dealership.enums.Role;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.PaymentRepository;
import com.dealership.repository.PurchaseRepository;
import com.dealership.service.impl.PaymentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ==========================================
    // CREATE PAYMENT SUCCESS
    // ==========================================

    @Test
    void shouldCreatePaymentSuccessfully() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentRequest request = PaymentRequest.builder()
                .purchaseId(1L)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .transactionId(request.getTransactionId())
                .paymentDate(request.getPaymentDate())
                .build();

        when(paymentRepository.existsByTransactionId("TXN1001"))
                .thenReturn(false);

        when(purchaseRepository.findById(1L))
                .thenReturn(Optional.of(purchase));

        when(paymentRepository.save(any(PaymentHistory.class)))
                .thenReturn(payment);

        PaymentResponse response =
                paymentService.createPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getTransactionId())
                .isEqualTo("TXN1001");

        verify(paymentRepository).save(any(PaymentHistory.class));
    }
    
    // ==========================================
    // DUPLICATE TRANSACTION ID
    // ==========================================

    @Test
    void shouldThrowDuplicateTransactionException() {

        PaymentRequest request = PaymentRequest.builder()
                .purchaseId(1L)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.existsByTransactionId("TXN1001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                paymentService.createPayment(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(paymentRepository, never())
                .save(any(PaymentHistory.class));
    }

    // ==========================================
    // PURCHASE NOT FOUND
    // ==========================================

    @Test
    void shouldThrowPurchaseNotFoundException() {

        PaymentRequest request = PaymentRequest.builder()
                .purchaseId(1L)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.existsByTransactionId("TXN1001"))
                .thenReturn(false);

        when(purchaseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentService.createPayment(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(paymentRepository, never())
                .save(any(PaymentHistory.class));
    }

    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================

    @Test
    void shouldGetPaymentById() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response =
                paymentService.getPaymentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTransactionId())
                .isEqualTo("TXN1001");
    }
    
    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================

    @Test
    void shouldReturnAllPayments() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findAll())
                .thenReturn(List.of(payment));

        List<PaymentResponse> payments =
                paymentService.getAllPayments();

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getTransactionId())
                .isEqualTo("TXN1001");
    }

    // ==========================================
    // GET PAYMENTS BY PURCHASE
    // ==========================================

    @Test
    void shouldReturnPaymentsByPurchase() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(purchaseRepository.findById(1L))
                .thenReturn(Optional.of(purchase));

        when(paymentRepository.findByPurchaseId(1L))
                .thenReturn(List.of(payment));

        List<PaymentResponse> responses =
                paymentService.getPaymentsByPurchase(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTransactionId())
                .isEqualTo("TXN1001");
    }
    
    // ==========================================
    // UPDATE PAYMENT STATUS
    // ==========================================

    @Test
    void shouldUpdatePaymentStatus() {

        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(BigDecimal.valueOf(1500000))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN1001")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(PaymentHistory.class)))
                .thenReturn(payment);

        PaymentResponse response =
                paymentService.updatePaymentStatus(
                        1L,
                        PaymentStatus.SUCCESS);

        assertThat(response.getPaymentStatus())
                .isEqualTo(PaymentStatus.SUCCESS);

        verify(paymentRepository)
                .save(any(PaymentHistory.class));
    }

    // ==========================================
    // DELETE PAYMENT
    // ==========================================

    @Test
    void shouldDeletePayment() {

        User user = User.builder()
                .id(1L)
                .role(Role.CUSTOMER)
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .build();

        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .transactionId("TXN1001")
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        paymentService.deletePayment(1L);

        verify(paymentRepository).delete(payment);
    }

}
    
    
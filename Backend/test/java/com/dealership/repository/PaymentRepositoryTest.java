package com.dealership.repository;

import com.dealership.entity.Customer;
import com.dealership.entity.PaymentHistory;
import com.dealership.entity.PurchaseHistory;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;
import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PaymentStatus;
import com.dealership.enums.PurchaseStatus;
import com.dealership.enums.Role;
import com.dealership.enums.VehicleStatus;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED PHASE: fails until PaymentRepository exists.
 */
class PaymentRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByPurchaseId_returnsPaymentHistory() {

        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@test.com")
                        .password("123456")
                        .role(Role.CUSTOMER)
                        .build());

        Customer customer = entityManager.persistAndFlush(
                Customer.builder()
                        .user(user)
                        .address("Ahmedabad")
                        .city("Ahmedabad")
                        .state("Gujarat")
                        .pincode("380001")
                        .build());

        Vehicle vehicle = entityManager.persistAndFlush(
                Vehicle.builder()
                        .brand("Hyundai")
                        .model("Creta")
                        .vinNumber("VIN1001")
                        .price(new BigDecimal("1500000"))
                        .quantity(5)
                        .status(VehicleStatus.AVAILABLE)
                        .build());

        PurchaseHistory purchase =
                entityManager.persistAndFlush(

                        PurchaseHistory.builder()
                                .customer(customer)
                                .vehicle(vehicle)
                                .purchaseDate(LocalDate.now())
                                .sellingPrice(new BigDecimal("1450000"))
                                .paymentMethod(PaymentMethod.UPI)
                                .purchaseStatus(PurchaseStatus.COMPLETED)
                                .build());

        PaymentHistory payment =
                PaymentHistory.builder()
                        .purchase(purchase)
                        .amount(new BigDecimal("1450000"))
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .transactionId("TXN10001")
                        .build();

        entityManager.persistAndFlush(payment);

        List<PaymentHistory> result =
                paymentRepository.findByPurchaseId(purchase.getId());

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getTransactionId())
                .isEqualTo("TXN10001");

        assertThat(result.get(0).getPaymentStatus())
                .isEqualTo(PaymentStatus.SUCCESS);

        assertThat(result.get(0).getAmount())
                .isEqualByComparingTo("1450000");
    }

    @Test
    void findByPurchaseId_returnsEmptyList_whenPurchaseHasNoPayments() {

        List<PaymentHistory> result =
                paymentRepository.findByPurchaseId(999L);

        assertThat(result).isEmpty();
    }
}
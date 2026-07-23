package com.dealership.repository;

import com.dealership.entity.*;
import com.dealership.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LoanRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByPurchaseId_returnsLoanDetails() {

        // ---------- User ----------
    	User user = User.builder()
    	        .firstName("Ronak")
    	        .lastName("Rathod")
    	        .email("ronak@test.com")
    	        .phone("9999999999")
    	        .password("password")
    	        .role(Role.CUSTOMER)
    	        .build();

        entityManager.persist(user);

        // ---------- Customer ----------
        Customer customer = Customer.builder()
                .user(user)
                .address("Ahmedabad")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380001")
                .build();

        entityManager.persist(customer);

        // ---------- Vehicle ----------
        Vehicle vehicle = Vehicle.builder()
                .brand("Mahindra")
                .model("XUV700")
                .vinNumber("VIN-LOAN1")
                .price(new BigDecimal("2500000"))
                .quantity(1)
                .status(VehicleStatus.AVAILABLE)
                .build();

        entityManager.persist(vehicle);

        // ---------- Purchase ----------
        PurchaseHistory purchase = PurchaseHistory.builder()
                .customer(customer)
                .vehicle(vehicle)
                .purchaseDate(LocalDate.now())
                .sellingPrice(new BigDecimal("2450000"))
                .paymentMethod(PaymentMethod.LOAN)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        entityManager.persist(purchase);

        // ---------- Loan ----------
        LoanDetails loan = LoanDetails.builder()
                .purchase(purchase)
                .bankName("SBI")
                .loanAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("8.75"))
                .tenure(60)
                .approvalStatus(LoanApprovalStatus.PENDING)
                .build();

        entityManager.persistAndFlush(loan);

        // ---------- Test ----------
        Optional<LoanDetails> result =
                loanRepository.findByPurchaseId(purchase.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getBankName()).isEqualTo("SBI");
    }
}
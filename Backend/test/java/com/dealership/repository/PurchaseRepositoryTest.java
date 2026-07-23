package com.dealership.repository;

import com.dealership.entity.*;
import com.dealership.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** RED PHASE: fails to compile until PurchaseRepository exists. */
class PurchaseRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByCustomerId_returnsThatCustomersPurchases() {
        User customerUser = entityManager.persistAndFlush(User.builder()
                .firstName("Dev").lastName("Shah").email("dev@example.com")
                .password("pw").role(Role.CUSTOMER).build());
        Customer customer = entityManager.persistAndFlush(Customer.builder()
                .user(customerUser).city("Ahmedabad").build());

        Vehicle vehicle = entityManager.persistAndFlush(Vehicle.builder()
                .brand("Tata").model("Nexon").vinNumber("VIN-P1")
                .price(new BigDecimal("1000000")).quantity(1)
                .status(VehicleStatus.AVAILABLE).build());

        PurchaseHistory purchase = PurchaseHistory.builder()
                .customer(customer).vehicle(vehicle)
                .purchaseDate(LocalDate.now())
                .sellingPrice(new BigDecimal("980000"))
                .paymentMethod(PaymentMethod.CASH)
                .purchaseStatus(PurchaseStatus.COMPLETED)
                .build();
        entityManager.persistAndFlush(purchase);

        List<PurchaseHistory> result = purchaseRepository.findByCustomerId(customer.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicle().getVinNumber()).isEqualTo("VIN-P1");
    }
}

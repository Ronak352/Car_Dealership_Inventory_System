package com.dealership.repository;

import com.dealership.entity.*;
import com.dealership.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** RED PHASE: fails to compile until WishlistRepository exists. */
class WishlistRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer() {
        User user = entityManager.persistAndFlush(User.builder()
                .firstName("Nisha").lastName("Iyer").email("nisha@example.com")
                .password("pw").role(Role.CUSTOMER).build());
        return entityManager.persistAndFlush(Customer.builder().user(user).city("Surat").build());
    }

    @Test
    void findByCustomerId_returnsWishlistItems() {
        Customer customer = customer();
        Vehicle vehicle = entityManager.persistAndFlush(Vehicle.builder()
                .brand("Skoda").model("Kushaq").vinNumber("VIN-WISH1")
                .price(new BigDecimal("1600000")).quantity(2)
                .status(VehicleStatus.AVAILABLE).build());

        entityManager.persistAndFlush(Wishlist.builder().customer(customer).vehicle(vehicle).build());

        List<Wishlist> result = wishlistRepository.findByCustomerId(customer.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehicle().getVinNumber()).isEqualTo("VIN-WISH1");
    }

    @Test
    void existsByCustomerIdAndVehicleId_detectsDuplicateWishlistEntry() {
        Customer customer = customer();
        Vehicle vehicle = entityManager.persistAndFlush(Vehicle.builder()
                .brand("VW").model("Taigun").vinNumber("VIN-WISH2")
                .price(new BigDecimal("1700000")).quantity(2)
                .status(VehicleStatus.AVAILABLE).build());

        entityManager.persistAndFlush(Wishlist.builder().customer(customer).vehicle(vehicle).build());

        assertThat(wishlistRepository.existsByCustomerIdAndVehicleId(customer.getId(), vehicle.getId()))
                .isTrue();
    }
}

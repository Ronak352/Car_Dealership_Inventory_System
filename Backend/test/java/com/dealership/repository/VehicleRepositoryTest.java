package com.dealership.repository;

import com.dealership.entity.Vehicle;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED PHASE: fails to compile until VehicleRepository exists under
 * com.dealership.repository.
 */
class VehicleRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Vehicle vehicle(String brand, String model, VehicleCategory category,
                            BigDecimal price, VehicleStatus status, String vin) {
        return Vehicle.builder()
                .brand(brand).model(model).category(category)
                .price(price).status(status).vinNumber(vin)
                .quantity(3)
                .build();
    }

    @Test
    void findByVinNumber_returnsVehicle_whenVinExists() {
        entityManager.persistAndFlush(vehicle("Toyota", "Fortuner", VehicleCategory.SUV,
                new BigDecimal("4500000"), VehicleStatus.AVAILABLE, "VIN-001"));

        Optional<Vehicle> found = vehicleRepository.findByVinNumber("VIN-001");

        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("Toyota");
    }

    @Test
    void existsByVinNumber_preventsDuplicates() {
        entityManager.persistAndFlush(vehicle("Honda", "City", VehicleCategory.SEDAN,
                new BigDecimal("1200000"), VehicleStatus.AVAILABLE, "VIN-002"));

        assertThat(vehicleRepository.existsByVinNumber("VIN-002")).isTrue();
        assertThat(vehicleRepository.existsByVinNumber("VIN-999")).isFalse();
    }

    @Test
    void findByStatus_returnsOnlyAvailableVehicles() {
        entityManager.persistAndFlush(vehicle("Hyundai", "Creta", VehicleCategory.SUV,
                new BigDecimal("1500000"), VehicleStatus.AVAILABLE, "VIN-003"));
        entityManager.persistAndFlush(vehicle("Hyundai", "Venue", VehicleCategory.SUV,
                new BigDecimal("1000000"), VehicleStatus.SOLD, "VIN-004"));

        List<Vehicle> available = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        assertThat(available).extracting(Vehicle::getVinNumber).containsExactly("VIN-003");
    }

    @Test
    void searchVehicles_filtersByBrandCategoryAndPriceRange() {
        entityManager.persistAndFlush(vehicle("Toyota", "Innova", VehicleCategory.SUV,
                new BigDecimal("2000000"), VehicleStatus.AVAILABLE, "VIN-005"));
        entityManager.persistAndFlush(vehicle("Toyota", "Glanza", VehicleCategory.HATCHBACK,
                new BigDecimal("800000"), VehicleStatus.AVAILABLE, "VIN-006"));
        entityManager.persistAndFlush(vehicle("Kia", "Seltos", VehicleCategory.SUV,
                new BigDecimal("1800000"), VehicleStatus.AVAILABLE, "VIN-007"));

        List<Vehicle> results = vehicleRepository.searchVehicles(
                "toyota", null, VehicleCategory.SUV,
                new BigDecimal("1000000"), new BigDecimal("3000000"));

        assertThat(results).extracting(Vehicle::getVinNumber).containsExactly("VIN-005");
    }
}

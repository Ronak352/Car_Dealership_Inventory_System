package com.dealership.repository;

import com.dealership.entity.InventoryLog;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;
import com.dealership.enums.InventoryOperation;
import com.dealership.enums.Role;
import com.dealership.enums.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByVehicleId_returnsInventoryLogs() {

        Vehicle vehicle = entityManager.persistAndFlush(
                Vehicle.builder()
                        .brand("Hyundai")
                        .model("Creta")
                        .vinNumber("VIN-INV-001")
                        .price(new BigDecimal("1500000"))
                        .quantity(10)
                        .status(VehicleStatus.AVAILABLE)
                        .build()
        );

        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .email("admin@test.com")
                        .password("password")
                        .role(Role.ADMIN)
                        .build()
        );

        InventoryLog log = InventoryLog.builder()
                .vehicle(vehicle)
                .performedBy(user)
                .operationType(InventoryOperation.ADD)
                .quantity(10)
                .build();

        entityManager.persistAndFlush(log);

        List<InventoryLog> logs =
                inventoryRepository.findByVehicleId(vehicle.getId());

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getOperationType())
                .isEqualTo(InventoryOperation.ADD);
    }

    @Test
    void findByVehicleIdOrderByDateDesc_returnsNewestFirst() {

        Vehicle vehicle = entityManager.persistAndFlush(
                Vehicle.builder()
                        .brand("Kia")
                        .model("Sonet")
                        .vinNumber("VIN-INV-002")
                        .price(new BigDecimal("1200000"))
                        .quantity(15)
                        .status(VehicleStatus.AVAILABLE)
                        .build()
        );

        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("Manager")
                        .lastName("User")
                        .email("manager@test.com")
                        .password("password")
                        .role(Role.MANAGER)
                        .build()
        );

        InventoryLog addLog = InventoryLog.builder()
                .vehicle(vehicle)
                .performedBy(user)
                .operationType(InventoryOperation.ADD)
                .quantity(15)
                .build();

        entityManager.persistAndFlush(addLog);

        InventoryLog removeLog = InventoryLog.builder()
                .vehicle(vehicle)
                .performedBy(user)
                .operationType(InventoryOperation.REMOVE)
                .quantity(2)
                .build();

        entityManager.persistAndFlush(removeLog);

        List<InventoryLog> logs =
                inventoryRepository.findByVehicleIdOrderByDateDesc(
                        vehicle.getId()
                );

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getOperationType())
                .isEqualTo(InventoryOperation.REMOVE);
    }

}
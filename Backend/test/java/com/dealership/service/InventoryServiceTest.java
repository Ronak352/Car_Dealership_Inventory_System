package com.dealership.service;

import com.dealership.dto.response.InventoryResponse;
import com.dealership.entity.InventoryLog;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;
import com.dealership.enums.InventoryOperation;
import com.dealership.enums.Role;
import com.dealership.enums.VehicleStatus;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.InventoryRepository;
import com.dealership.repository.UserRepository;
import com.dealership.repository.VehicleRepository;
import com.dealership.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldIncreaseStock() {

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("Hyundai")
                .model("Creta")
                .vinNumber("VIN001")
                .price(BigDecimal.valueOf(1500000))
                .quantity(10)
                .status(VehicleStatus.AVAILABLE)
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();

        InventoryLog log = InventoryLog.builder()
                .id(1L)
                .vehicle(vehicle)
                .performedBy(user)
                .operationType(InventoryOperation.ADD)
                .quantity(5)
                .build();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(inventoryRepository.save(any(InventoryLog.class)))
                .thenReturn(log);

        InventoryResponse response =
                inventoryService.increaseStock(1L, 5, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getOperationType())
                .isEqualTo(InventoryOperation.ADD);

        assertThat(response.getAvailableQuantity())
                .isEqualTo(15);

        verify(vehicleRepository).save(vehicle);
        verify(inventoryRepository).save(any(InventoryLog.class));
    }


@Test
void shouldDecreaseStock() {

    Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .brand("Hyundai")
            .model("Creta")
            .vinNumber("VIN001")
            .price(BigDecimal.valueOf(1500000))
            .quantity(10)
            .status(VehicleStatus.AVAILABLE)
            .build();

    User user = User.builder()
            .id(1L)
            .firstName("Admin")
            .lastName("User")
            .role(Role.ADMIN)
            .build();

    InventoryLog log = InventoryLog.builder()
            .id(2L)
            .vehicle(vehicle)
            .performedBy(user)
            .operationType(InventoryOperation.REMOVE)
            .quantity(3)
            .build();

    when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(inventoryRepository.save(any(InventoryLog.class)))
            .thenReturn(log);

    InventoryResponse response =
            inventoryService.decreaseStock(1L, 3, 1L);

    assertThat(response).isNotNull();
    assertThat(response.getOperationType())
            .isEqualTo(InventoryOperation.REMOVE);

    assertThat(response.getAvailableQuantity())
            .isEqualTo(7);

    verify(vehicleRepository).save(vehicle);
    verify(inventoryRepository).save(any(InventoryLog.class));
}

@Test
void shouldThrowWhenStockIsInsufficient() {

    Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .quantity(2)
            .build();

    User user = User.builder()
            .id(1L)
            .build();

    when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            inventoryService.decreaseStock(1L, 5, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Insufficient vehicle stock.");

    verify(inventoryRepository, never()).save(any());
}

@Test
void shouldThrowWhenVehicleNotFound() {

    when(vehicleRepository.findById(1L))
            .thenReturn(Optional.empty());

    org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            inventoryService.increaseStock(1L, 5, 1L))
            .isInstanceOf(ResourceNotFoundException.class);

    verify(inventoryRepository, never()).save(any());
}
@Test
void shouldUpdateStock() {

    Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .brand("Hyundai")
            .model("Creta")
            .vinNumber("VIN001")
            .price(BigDecimal.valueOf(1500000))
            .quantity(10)
            .status(VehicleStatus.AVAILABLE)
            .build();

    User user = User.builder()
            .id(1L)
            .firstName("Admin")
            .lastName("User")
            .role(Role.ADMIN)
            .build();

    InventoryLog log = InventoryLog.builder()
            .id(3L)
            .vehicle(vehicle)
            .performedBy(user)
            .operationType(InventoryOperation.UPDATE)
            .quantity(10) // difference (20 - 10)
            .build();

    when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(inventoryRepository.save(any(InventoryLog.class)))
            .thenReturn(log);

    InventoryResponse response =
            inventoryService.updateStock(1L, 20, 1L);

    assertThat(response).isNotNull();
    assertThat(response.getOperationType())
            .isEqualTo(InventoryOperation.UPDATE);

    assertThat(response.getAvailableQuantity())
            .isEqualTo(20);

    verify(vehicleRepository).save(vehicle);
    verify(inventoryRepository).save(any(InventoryLog.class));
}

@Test
void shouldGetInventoryHistory() {

    Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .brand("Hyundai")
            .model("Creta")
            .vinNumber("VIN001")
            .quantity(10)
            .build();

    User user = User.builder()
            .id(1L)
            .firstName("Admin")
            .lastName("User")
            .build();

    InventoryLog log = InventoryLog.builder()
            .id(1L)
            .vehicle(vehicle)
            .performedBy(user)
            .operationType(InventoryOperation.ADD)
            .quantity(10)
            .build();

    when(inventoryRepository.findByVehicleIdOrderByDateDesc(1L))
            .thenReturn(java.util.List.of(log));

    java.util.List<InventoryResponse> responses =
            inventoryService.getInventoryHistory(1L);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getVehicleId()).isEqualTo(1L);
    assertThat(responses.get(0).getOperationType())
            .isEqualTo(InventoryOperation.ADD);

    verify(inventoryRepository)
            .findByVehicleIdOrderByDateDesc(1L);
}
@Test
void shouldReturnAvailableQuantity() {

    Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .quantity(25)
            .build();

    when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

    Integer quantity =
            inventoryService.getAvailableQuantity(1L);

    assertThat(quantity).isEqualTo(25);

    verify(vehicleRepository).findById(1L);
}

@Test
void shouldReturnLowStockVehicles() {

    Vehicle lowStockVehicle = Vehicle.builder()
            .id(1L)
            .brand("Hyundai")
            .model("i20")
            .variant("Sportz")
            .vinNumber("VIN001")
            .price(BigDecimal.valueOf(900000))
            .quantity(3)
            .status(VehicleStatus.AVAILABLE)
            .build();

    Vehicle normalStockVehicle = Vehicle.builder()
            .id(2L)
            .brand("Honda")
            .model("City")
            .variant("ZX")
            .vinNumber("VIN002")
            .price(BigDecimal.valueOf(1500000))
            .quantity(20)
            .status(VehicleStatus.AVAILABLE)
            .build();

    when(vehicleRepository.findAll())
            .thenReturn(java.util.List.of(
                    lowStockVehicle,
                    normalStockVehicle
            ));

    java.util.List<com.dealership.dto.response.VehicleResponse> responses =
            inventoryService.getLowStockVehicles(5);

    assertThat(responses).hasSize(1);

    assertThat(responses.get(0).getBrand())
            .isEqualTo("Hyundai");

    assertThat(responses.get(0).getQuantity())
            .isEqualTo(3);

    verify(vehicleRepository).findAll();
}

}


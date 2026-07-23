package com.dealership.service.impl;

import com.dealership.dto.response.InventoryResponse;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.entity.InventoryLog;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;
import com.dealership.enums.InventoryOperation;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.InventoryRepository;
import com.dealership.repository.UserRepository;
import com.dealership.repository.VehicleRepository;
import com.dealership.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    // ==========================================
    // INCREASE STOCK
    // ==========================================

    @Override
    public InventoryResponse increaseStock(
            Long vehicleId,
            Integer quantity,
            Long performedByUserId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + vehicleId));

        User user = userRepository.findById(performedByUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + performedByUserId));

        vehicle.setQuantity(vehicle.getQuantity() + quantity);

        vehicleRepository.save(vehicle);

        InventoryLog log = InventoryLog.builder()
                .vehicle(vehicle)
                .operationType(InventoryOperation.ADD)
                .quantity(quantity)
                .performedBy(user)
                .build();

        InventoryLog savedLog = inventoryRepository.save(log);

        return mapToResponse(savedLog);
    }
    
    // ==========================================
    // DECREASE STOCK
    // ==========================================

    @Override
    public InventoryResponse decreaseStock(
            Long vehicleId,
            Integer quantity,
            Long performedByUserId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + vehicleId));

        User user = userRepository.findById(performedByUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + performedByUserId));

        if (vehicle.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient vehicle stock."
            );
        }

        vehicle.setQuantity(vehicle.getQuantity() - quantity);

        vehicleRepository.save(vehicle);

        InventoryLog log = InventoryLog.builder()
                .vehicle(vehicle)
                .operationType(InventoryOperation.REMOVE)
                .quantity(quantity)
                .performedBy(user)
                .build();

        InventoryLog savedLog = inventoryRepository.save(log);

        return mapToResponse(savedLog);
    }

    // ==========================================
    // UPDATE STOCK
    // ==========================================

    @Override
    public InventoryResponse updateStock(
            Long vehicleId,
            Integer newQuantity,
            Long performedByUserId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + vehicleId));

        User user = userRepository.findById(performedByUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + performedByUserId));

        int difference = newQuantity - vehicle.getQuantity();

        vehicle.setQuantity(newQuantity);

        vehicleRepository.save(vehicle);

        InventoryLog log = InventoryLog.builder()
                .vehicle(vehicle)
                .operationType(InventoryOperation.UPDATE)
                .quantity(difference)
                .performedBy(user)
                .build();

        InventoryLog savedLog = inventoryRepository.save(log);

        return mapToResponse(savedLog);
    }
    
    // ==========================================
    // INVENTORY HISTORY
    // ==========================================

    @Override
    public List<InventoryResponse> getInventoryHistory(Long vehicleId) {

        return inventoryRepository
                .findByVehicleIdOrderByDateDesc(vehicleId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // AVAILABLE QUANTITY
    // ==========================================

    @Override
    public Integer getAvailableQuantity(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with id: " + vehicleId));

        return vehicle.getQuantity();
    }

    // ==========================================
    // LOW STOCK VEHICLES
    // ==========================================

    @Override
    public List<VehicleResponse> getLowStockVehicles(Integer threshold) {

        return vehicleRepository.findAll()
                .stream()
                .filter(vehicle ->
                        vehicle.getQuantity() != null
                                && vehicle.getQuantity() <= threshold)
                .map(vehicle -> VehicleResponse.builder()
                        .id(vehicle.getId())
                        .brand(vehicle.getBrand())
                        .model(vehicle.getModel())
                        .variant(vehicle.getVariant())
                        .vinNumber(vehicle.getVinNumber())
                        .price(vehicle.getPrice())
                        .quantity(vehicle.getQuantity())
                        .category(vehicle.getCategory())
                        .status(vehicle.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    // ==========================================
    // MAPPER
    // ==========================================

    private InventoryResponse mapToResponse(InventoryLog log) {

        Vehicle vehicle = log.getVehicle();

        return InventoryResponse.builder()
                .id(log.getId())
                .vehicleId(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .vinNumber(vehicle.getVinNumber())
                .operationType(log.getOperationType())
                .quantity(log.getQuantity())
                .availableQuantity(vehicle.getQuantity())
                .date(log.getDate())
                .performedById(
                        log.getPerformedBy() != null
                                ? log.getPerformedBy().getId()
                                : null)
                .performedByName(
                        log.getPerformedBy() != null
                                ? log.getPerformedBy().getFirstName()
                                        + " "
                                        + log.getPerformedBy().getLastName()
                                : null)
                .build();
    }

}
    
    
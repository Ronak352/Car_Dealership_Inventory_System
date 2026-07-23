package com.dealership.repository;

import com.dealership.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByVehicleId(Long vehicleId);

    List<InventoryLog> findByVehicleIdOrderByDateDesc(Long vehicleId);
}

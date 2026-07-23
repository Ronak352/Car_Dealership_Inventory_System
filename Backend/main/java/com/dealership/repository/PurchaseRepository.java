package com.dealership.repository;

import com.dealership.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<PurchaseHistory, Long> {

    List<PurchaseHistory> findByCustomerId(Long customerId);

    List<PurchaseHistory> findByVehicleId(Long vehicleId);

    List<PurchaseHistory> findBySalespersonId(Long salespersonId);
}

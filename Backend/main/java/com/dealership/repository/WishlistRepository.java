package com.dealership.repository;

import com.dealership.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndVehicleId(Long customerId, Long vehicleId);

    void deleteByCustomerIdAndVehicleId(Long customerId, Long vehicleId);
}

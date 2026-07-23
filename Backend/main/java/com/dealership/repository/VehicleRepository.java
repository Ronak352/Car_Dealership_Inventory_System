package com.dealership.repository;

import com.dealership.entity.Vehicle;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVinNumber(String vinNumber);

    boolean existsByVinNumber(String vinNumber);

    List<Vehicle> findByStatus(VehicleStatus status);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE (:brand IS NULL OR LOWER(v.brand) = :brand)
              AND (:model IS NULL OR LOWER(v.model) = :model)
              AND (:category IS NULL OR v.category = :category)
              AND (:minPrice IS NULL OR v.price >= :minPrice)
              AND (:maxPrice IS NULL OR v.price <= :maxPrice)
            """)
    List<Vehicle> searchVehicles(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("category") VehicleCategory category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
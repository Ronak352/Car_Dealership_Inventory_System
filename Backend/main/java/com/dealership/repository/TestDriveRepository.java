package com.dealership.repository;


import com.dealership.entity.TestDriveBooking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;



@Repository
public interface TestDriveRepository 
        extends JpaRepository<TestDriveBooking, Long> {



    List<TestDriveBooking> findByCustomerId(Long customerId);



    List<TestDriveBooking> findByVehicleId(Long vehicleId);



    List<TestDriveBooking> findBySalespersonId(Long salespersonId);


}
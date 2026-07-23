package com.dealership.service;


import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.TestDriveBookingResponse;


import java.util.List;


public interface TestDriveService {


    TestDriveBookingResponse createBooking(
            TestDriveBookingRequest request
    );


    TestDriveBookingResponse getBookingById(
            Long id
    );


    List<TestDriveBookingResponse> getAllBookings();


    List<TestDriveBookingResponse> getBookingsByCustomer(
            Long customerId
    );


    List<TestDriveBookingResponse> getBookingsByVehicle(
            Long vehicleId
    );


    TestDriveBookingResponse assignSalesperson(
            Long bookingId,
            Long salespersonId
    );


    TestDriveBookingResponse updateStatus(
            Long bookingId,
            String status
    );


    void deleteBooking(Long id);

}
package com.dealership.service.impl;


import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.TestDriveBookingResponse;


import com.dealership.entity.Customer;
import com.dealership.entity.Employee;
import com.dealership.entity.TestDriveBooking;
import com.dealership.entity.Vehicle;


import com.dealership.enums.TestDriveStatus;


import com.dealership.exception.ResourceNotFoundException;


import com.dealership.repository.CustomerRepository;
import com.dealership.repository.EmployeeRepository;
import com.dealership.repository.TestDriveRepository;
import com.dealership.repository.VehicleRepository;


import com.dealership.service.TestDriveService;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;


import java.util.List;



@Service
@RequiredArgsConstructor
public class TestDriveServiceImpl implements TestDriveService {



    private final TestDriveRepository testDriveRepository;


    private final CustomerRepository customerRepository;


    private final VehicleRepository vehicleRepository;


    private final EmployeeRepository employeeRepository;





    @Override
    public TestDriveBookingResponse createBooking(
            TestDriveBookingRequest request
    ){


        Customer customer =
                customerRepository.findById(
                        request.getCustomerId()
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer not found"
                        )
                );



        Vehicle vehicle =
                vehicleRepository.findById(
                        request.getVehicleId()
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle not found"
                        )
                );



        TestDriveBooking booking =
                TestDriveBooking.builder()

                .customer(customer)

                .vehicle(vehicle)

                .bookingDate(
                        request.getBookingDate()
                )

                .testDriveDate(
                        request.getTestDriveDate()
                )

                .status(
                        TestDriveStatus.REQUESTED
                )

                .build();



        return mapToResponse(
                testDriveRepository.save(booking)
        );

    }







    @Override
    public TestDriveBookingResponse getBookingById(
            Long id
    ){


        TestDriveBooking booking =
                testDriveRepository.findById(id)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Test drive booking not found"
                        )
                );


        return mapToResponse(booking);

    }








    @Override
    public List<TestDriveBookingResponse> getAllBookings(){


        return testDriveRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }









    @Override
    public List<TestDriveBookingResponse> getBookingsByCustomer(
            Long customerId
    ){


        return testDriveRepository
                .findByCustomerId(customerId)

                .stream()

                .map(this::mapToResponse)

                .toList();

    }









    @Override
    public List<TestDriveBookingResponse> getBookingsByVehicle(
            Long vehicleId
    ){


        return testDriveRepository
                .findByVehicleId(vehicleId)

                .stream()

                .map(this::mapToResponse)

                .toList();

    }









    @Override
    public TestDriveBookingResponse assignSalesperson(
            Long bookingId,
            Long salespersonId
    ){


        TestDriveBooking booking =
                testDriveRepository.findById(bookingId)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Booking not found"
                        )
                );



        Employee employee =
                employeeRepository.findById(salespersonId)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Salesperson not found"
                        )
                );



        booking.setSalesperson(employee);



        return mapToResponse(
                testDriveRepository.save(booking)
        );

    }









    @Override
    public TestDriveBookingResponse updateStatus(
            Long bookingId,
            String status
    ){


        TestDriveBooking booking =
                testDriveRepository.findById(bookingId)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Booking not found"
                        )
                );



        booking.setStatus(
                TestDriveStatus.valueOf(status)
        );



        return mapToResponse(
                testDriveRepository.save(booking)
        );

    }









    @Override
    public void deleteBooking(
            Long id
    ){


        TestDriveBooking booking =
                testDriveRepository.findById(id)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Booking not found"
                        )
                );



        testDriveRepository.delete(booking);

    }









    private TestDriveBookingResponse mapToResponse(
            TestDriveBooking booking
    ){


        String customerName =
                booking.getCustomer()
                .getUser()
                .getFirstName()
                + " "
                +
                booking.getCustomer()
                .getUser()
                .getLastName();



        String vehicleName =
                booking.getVehicle()
                .getBrand()
                + " "
                +
                booking.getVehicle()
                .getModel();



        String salespersonName = null;



        if(booking.getSalesperson()!=null){


            salespersonName =
                    booking.getSalesperson()
                    .getUser()
                    .getFirstName()
                    + " "
                    +
                    booking.getSalesperson()
                    .getUser()
                    .getLastName();

        }



        return TestDriveBookingResponse.builder()

                .id(booking.getId())

                .customerName(customerName)

                .vehicleName(vehicleName)

                .salespersonName(salespersonName)

                .bookingDate(
                        booking.getBookingDate()
                )

                .testDriveDate(
                        booking.getTestDriveDate()
                )

                .status(
                        booking.getStatus()
                )

                .build();

    }


}
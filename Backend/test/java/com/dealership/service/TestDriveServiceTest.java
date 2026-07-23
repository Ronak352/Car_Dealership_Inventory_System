package com.dealership.service;


import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.TestDriveBookingResponse;
import com.dealership.entity.*;
import com.dealership.enums.Role;
import com.dealership.enums.TestDriveStatus;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.*;
import com.dealership.service.impl.TestDriveServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



class TestDriveServiceTest {



    @Mock
    private TestDriveRepository testDriveRepository;


    @Mock
    private CustomerRepository customerRepository;


    @Mock
    private VehicleRepository vehicleRepository;


    @Mock
    private EmployeeRepository employeeRepository;



    @InjectMocks
    private TestDriveServiceImpl testDriveService;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }






    @Test
    void shouldCreateBookingSuccessfully(){


        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();


        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();



        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .build();



        TestDriveBooking booking =
                TestDriveBooking.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(2))
                .status(TestDriveStatus.REQUESTED)
                .build();



        TestDriveBookingRequest request =
                TestDriveBookingRequest.builder()
                .customerId(1L)
                .vehicleId(1L)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(2))
                .build();



        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));


        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));


        when(testDriveRepository.save(any(TestDriveBooking.class)))
                .thenReturn(booking);



        TestDriveBookingResponse response =
                testDriveService.createBooking(request);



        assertThat(response).isNotNull();

        assertThat(response.getStatus())
                .isEqualTo(TestDriveStatus.REQUESTED);



        verify(testDriveRepository)
                .save(any(TestDriveBooking.class));

    }








    @Test
    void shouldThrowExceptionWhenCustomerNotFound(){



        TestDriveBookingRequest request =
                TestDriveBookingRequest.builder()
                .customerId(1L)
                .vehicleId(1L)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now())
                .build();



        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());



        assertThatThrownBy(
                () -> testDriveService.createBooking(request)
        )
        .isInstanceOf(ResourceNotFoundException.class);



        verify(vehicleRepository,never())
                .findById(any());

    }
    
    
    @Test
    void shouldGetBookingById(){


        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();



        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();



        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .build();



        TestDriveBooking booking =
                TestDriveBooking.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(2))
                .status(TestDriveStatus.REQUESTED)
                .build();



        when(testDriveRepository.findById(1L))
                .thenReturn(Optional.of(booking));



        TestDriveBookingResponse response =
                testDriveService.getBookingById(1L);



        assertThat(response)
                .isNotNull();


        assertThat(response.getCustomerName())
                .isEqualTo("John Smith");


        assertThat(response.getVehicleName())
                .isEqualTo("BMW X5");


        assertThat(response.getStatus())
                .isEqualTo(TestDriveStatus.REQUESTED);

    }















    @Test
    void shouldReturnAllBookings(){



        when(testDriveRepository.findAll())
                .thenReturn(List.of());


        List<TestDriveBookingResponse> response =
                testDriveService.getAllBookings();



        assertThat(response)
                .isEmpty();

    }





    @Test
    void shouldUpdateStatus(){


        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .role(Role.CUSTOMER)
                .build();


        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();



        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .build();



        TestDriveBooking booking =
                TestDriveBooking.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .status(TestDriveStatus.REQUESTED)
                .build();



        when(testDriveRepository.findById(1L))
                .thenReturn(Optional.of(booking));


        when(testDriveRepository.save(any()))
                .thenReturn(booking);



        TestDriveBookingResponse response =
                testDriveService.updateStatus(
                        1L,
                        "APPROVED"
                );


        assertThat(response)
                .isNotNull();

    }




    @Test
    void shouldDeleteBooking(){



        TestDriveBooking booking =
                TestDriveBooking.builder()
                .id(1L)
                .build();



        when(testDriveRepository.findById(1L))
                .thenReturn(Optional.of(booking));



        testDriveService.deleteBooking(1L);



        verify(testDriveRepository)
                .delete(booking);

    }

}
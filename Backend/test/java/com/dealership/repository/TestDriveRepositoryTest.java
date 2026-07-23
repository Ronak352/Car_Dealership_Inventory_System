package com.dealership.repository;


import com.dealership.entity.Customer;
import com.dealership.entity.TestDriveBooking;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;

import com.dealership.enums.Role;
import com.dealership.enums.TestDriveStatus;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;



class TestDriveRepositoryTest extends AbstractRepositoryTest {



    @Autowired
    private TestDriveRepository testDriveRepository;



    @Autowired
    private TestEntityManager entityManager;





    @Test
    void findByCustomerId_returnsCustomerBookings() {



        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .email("john@test.com")
                        .password("12345")
                        .role(Role.CUSTOMER)
                        .build()
        );



        Customer customer = entityManager.persistAndFlush(
                Customer.builder()
                        .user(user)
                        .address("Ahmedabad")
                        .city("Ahmedabad")
                        .state("Gujarat")
                        .pincode("380001")
                        .build()
        );



        Vehicle vehicle = entityManager.persistAndFlush(
                Vehicle.builder()
                        .brand("BMW")
                        .model("X5")
                        .vinNumber("VIN-TD-001")
                        .price(new BigDecimal("5000000"))
                        .quantity(1)
                        .build()
        );



        TestDriveBooking booking =
                TestDriveBooking.builder()
                        .customer(customer)
                        .vehicle(vehicle)
                        .bookingDate(LocalDate.now())
                        .testDriveDate(LocalDate.now().plusDays(2))
                        .status(TestDriveStatus.REQUESTED)
                        .build();



        entityManager.persistAndFlush(booking);



        List<TestDriveBooking> result =
                testDriveRepository.findByCustomerId(customer.getId());



        assertThat(result)
                .hasSize(1);



        assertThat(result.get(0).getCustomer().getId())
                .isEqualTo(customer.getId());

    }







    @Test
    void findByVehicleId_returnsVehicleBookings() {



        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("Alex")
                        .lastName("Brown")
                        .email("alex@test.com")
                        .password("12345")
                        .role(Role.CUSTOMER)
                        .build()
        );



        Customer customer = entityManager.persistAndFlush(
                Customer.builder()
                        .user(user)
                        .build()
        );



        Vehicle vehicle = entityManager.persistAndFlush(
                Vehicle.builder()
                        .brand("Toyota")
                        .model("Fortuner")
                        .vinNumber("VIN-TD-002")
                        .price(new BigDecimal("3500000"))
                        .quantity(2)
                        .build()
        );



        TestDriveBooking booking =
                TestDriveBooking.builder()
                        .customer(customer)
                        .vehicle(vehicle)
                        .bookingDate(LocalDate.now())
                        .testDriveDate(LocalDate.now())
                        .status(TestDriveStatus.REQUESTED)
                        .build();



        entityManager.persistAndFlush(booking);



        List<TestDriveBooking> result =
                testDriveRepository.findByVehicleId(vehicle.getId());



        assertThat(result)
                .hasSize(1);



        assertThat(result.get(0).getVehicle().getId())
                .isEqualTo(vehicle.getId());

    }







    @Test
    void findAll_returnsAllBookings() {



        List<TestDriveBooking> bookings =
                testDriveRepository.findAll();



        assertThat(bookings)
                .isNotNull();

    }

}
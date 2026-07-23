package com.dealership.service;


import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.PurchaseResponse;

import com.dealership.entity.Customer;
import com.dealership.entity.PurchaseHistory;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import com.dealership.enums.VehicleStatus;

import com.dealership.exception.ResourceNotFoundException;

import com.dealership.repository.CustomerRepository;
import com.dealership.repository.PurchaseRepository;
import com.dealership.repository.VehicleRepository;

import com.dealership.service.impl.PurchaseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;



class PurchaseServiceImplTest {



    @Mock
    private PurchaseRepository purchaseRepository;


    @Mock
    private CustomerRepository customerRepository;


    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private PurchaseServiceImpl purchaseService;




    private Customer customer;

    private Vehicle vehicle;

    private PurchaseRequest request;



    @BeforeEach
    void setup(){


        MockitoAnnotations.openMocks(this);



        User user =
                User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@gmail.com")
                .build();



        customer =
                Customer.builder()
                .id(1L)
                .user(user)
                .build();




        vehicle =
                Vehicle.builder()
                .id(1L)
                .brand("Toyota")
                .model("Fortuner")
                .quantity(5)
                .status(VehicleStatus.AVAILABLE)
                .build();




        request =
                PurchaseRequest.builder()

                .customerId(1L)

                .vehicleId(1L)

                .purchaseDate(
                        LocalDate.now()
                )

                .sellingPrice(
                        new BigDecimal("4000000")
                )

                .paymentMethod(
                        PaymentMethod.CASH
                )

                .purchaseStatus(
                        PurchaseStatus.BOOKED
                )

                .build();

    }





    @Test
    void shouldCreatePurchaseSuccessfully(){



        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));



        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));



        PurchaseHistory saved =
                PurchaseHistory.builder()

                .id(1L)

                .customer(customer)

                .vehicle(vehicle)

                .purchaseDate(
                        request.getPurchaseDate()
                )

                .sellingPrice(
                        request.getSellingPrice()
                )

                .paymentMethod(
                        request.getPaymentMethod()
                )

                .purchaseStatus(
                        request.getPurchaseStatus()
                )

                .build();



        when(purchaseRepository.save(any(PurchaseHistory.class)))
                .thenReturn(saved);



        PurchaseResponse response =
                purchaseService.createPurchase(request);



        assertThat(response).isNotNull();

        assertThat(response.getSellingPrice())
                .isEqualByComparingTo(
                        new BigDecimal("4000000")
                );


        verify(vehicleRepository)
                .save(vehicle);


    }





    @Test
    void shouldThrowExceptionWhenCustomerNotFound(){


        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());



        assertThatThrownBy(
                () -> purchaseService.createPurchase(request)
        )

        .isInstanceOf(ResourceNotFoundException.class)

        .hasMessage(
                "Customer not found"
        );


    }





    @Test
    void shouldThrowExceptionWhenVehicleNotFound(){


        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));


        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());



        assertThatThrownBy(
                () -> purchaseService.createPurchase(request)
        )

        .isInstanceOf(ResourceNotFoundException.class)

        .hasMessage(
                "Vehicle not found"
        );

    }





    @Test
    void shouldThrowExceptionWhenVehicleOutOfStock(){



        vehicle.setQuantity(0);



        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));


        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));



        assertThatThrownBy(
                () -> purchaseService.createPurchase(request)
        )

        .isInstanceOf(RuntimeException.class)

        .hasMessage(
                "Vehicle is out of stock"
        );

    }



}
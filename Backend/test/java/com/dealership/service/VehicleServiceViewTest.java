package com.dealership.service;


import com.dealership.dto.response.VehicleResponse;

import com.dealership.entity.Vehicle;

import com.dealership.enums.VehicleStatus;

import com.dealership.repository.VehicleRepository;

import com.dealership.service.impl.VehicleServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;




class VehicleServiceViewTest {



    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private VehicleServiceImpl vehicleService;




    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }






    // ==========================================
    // TEST 1
    // Available vehicles should be returned
    // ==========================================


    @Test
    void availableVehiclesShouldBeReturned(){



        Vehicle vehicle =

                Vehicle.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .vinNumber("VIN123456")

                .price(
                        new BigDecimal("4500000")
                )

                .quantity(5)

                .status(
                        VehicleStatus.AVAILABLE
                )

                .build();




        when(
                vehicleRepository.findByStatus(
                        VehicleStatus.AVAILABLE
                )
        )
        .thenReturn(List.of(vehicle));




        List<VehicleResponse> response =

                vehicleService.getAvailableVehicles();




        assertThat(response)
                .isNotEmpty();



        assertThat(response.get(0).getBrand())
                .isEqualTo("Toyota");




        verify(vehicleRepository)

                .findByStatus(
                        VehicleStatus.AVAILABLE
                );


    }







    // ==========================================
    // TEST 2
    // Empty inventory
    // ==========================================


    @Test
    void emptyInventoryShouldReturnEmptyList(){



        when(
                vehicleRepository.findByStatus(
                        VehicleStatus.AVAILABLE
                )
        )
        .thenReturn(List.of());




        List<VehicleResponse> response =

                vehicleService.getAvailableVehicles();




        assertThat(response)
                .isEmpty();



    }







    // ==========================================
    // TEST 3
    // Only AVAILABLE vehicles fetched
    // ==========================================


    @Test
    void onlyAvailableVehiclesShouldBeFetched(){



        when(
                vehicleRepository.findByStatus(
                        VehicleStatus.AVAILABLE
                )
        )
        .thenReturn(List.of());




        vehicleService.getAvailableVehicles();




        verify(vehicleRepository,times(1))

                .findByStatus(
                        VehicleStatus.AVAILABLE
                );


    }



}
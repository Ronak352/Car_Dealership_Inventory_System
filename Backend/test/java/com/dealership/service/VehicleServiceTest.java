package com.dealership.service;


import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.VehicleResponse;

import com.dealership.entity.Vehicle;

import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;

import com.dealership.enums.FuelType;
import com.dealership.enums.Transmission;


import com.dealership.exception.DuplicateResourceException;

import com.dealership.repository.VehicleRepository;


import com.dealership.service.impl.VehicleServiceImpl;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



class VehicleServiceTest {



    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private VehicleServiceImpl vehicleService;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }




    private VehicleRequest createVehicleRequest(){


        return VehicleRequest.builder()

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .category(VehicleCategory.SUV)

                .fuelType(FuelType.DIESEL)

                .transmission(Transmission.AUTOMATIC)

                .manufacturingYear(2026)

                .color("Black")

                .engineNumber("ENG12345")

                .vinNumber("VIN123456")

                .price(
                        new BigDecimal("4500000")
                )

                .discount(
                        new BigDecimal("100000")
                )

                .quantity(5)

                .condition(
                        VehicleCondition.NEW
                )

                .status(
                        VehicleStatus.AVAILABLE
                )

                .build();

    }





    // ==========================================
    // TEST 1
    // Admin can add vehicle successfully
    // ==========================================


    @Test
    void adminShouldAddVehicleSuccessfully(){


        VehicleRequest request =
                createVehicleRequest();



        Vehicle vehicle =
                Vehicle.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .vinNumber("VIN123456")

                .quantity(5)

                .build();



        when(
                vehicleRepository.save(any(Vehicle.class))
        )
        .thenReturn(vehicle);




        VehicleResponse response =
                vehicleService.addVehicle(request);



        assertThat(response)
                .isNotNull();



        assertThat(response.getBrand())
                .isEqualTo("Toyota");



        verify(vehicleRepository)
                .save(any(Vehicle.class));

    }





    // ==========================================
    // TEST 2
    // Duplicate VIN rejected
    // ==========================================


    @Test
    void duplicateVinShouldBeRejected(){



        VehicleRequest request =
                createVehicleRequest();



        when(
                vehicleRepository.existsByVinNumber(
                        request.getVinNumber()
                )
        )
        .thenReturn(true);



        assertThrows(

                DuplicateResourceException.class,

                () ->
                        vehicleService.addVehicle(request)

        );



        verify(vehicleRepository,never())
                .save(any(Vehicle.class));

    }





    // ==========================================
    // TEST 3
    // Vehicle details saved correctly
    // ==========================================


    @Test
    void vehicleDetailsShouldSaveCorrectly(){



        VehicleRequest request =
                createVehicleRequest();



        Vehicle savedVehicle =

                Vehicle.builder()

                .id(10L)

                .brand(request.getBrand())

                .model(request.getModel())

                .vinNumber(request.getVinNumber())

                .price(request.getPrice())

                .quantity(request.getQuantity())

                .build();



        when(
                vehicleRepository.save(any(Vehicle.class))
        )
        .thenReturn(savedVehicle);




        VehicleResponse response =

                vehicleService.addVehicle(request);




        assertThat(response.getBrand())
                .isEqualTo("Toyota");

        assertThat(response.getModel())
                .isEqualTo("Fortuner");


        assertThat(response.getVinNumber())
                .isEqualTo("VIN123456");



        assertThat(response.getQuantity())
                .isEqualTo(5);



    }


}
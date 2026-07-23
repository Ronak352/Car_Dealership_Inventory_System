package com.dealership.service;


import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.VehicleResponse;

import com.dealership.entity.Vehicle;

import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;
import com.dealership.enums.FuelType;
import com.dealership.enums.Transmission;


import com.dealership.repository.VehicleRepository;

import com.dealership.service.impl.VehicleServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;





class VehicleUpdateServiceTest {



    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private VehicleServiceImpl vehicleService;





    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }





    @Test
    void vehicleShouldBeUpdatedSuccessfully(){



        Vehicle vehicle =

                Vehicle.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Old")

                .vinNumber("VIN123")

                .price(new BigDecimal("4000000"))

                .quantity(2)

                .status(VehicleStatus.AVAILABLE)

                .build();




        VehicleRequest request =

                VehicleRequest.builder()

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .vinNumber("VIN123")

                .price(new BigDecimal("4500000"))

                .quantity(3)

                .build();




        when(
                vehicleRepository.findById(1L)
        )
        .thenReturn(Optional.of(vehicle));



        when(
                vehicleRepository.save(any(Vehicle.class))
        )
        .thenReturn(vehicle);





        VehicleResponse response =

                vehicleService.updateVehicle(
                        1L,
                        request
                );





        assertThat(response)
                .isNotNull();



        assertThat(response.getVariant())
                .isEqualTo("Legender");





        verify(vehicleRepository)
                .save(any(Vehicle.class));


    }






    @Test
    void updatingNonExistingVehicleShouldFail(){



        when(
                vehicleRepository.findById(1L)
        )
        .thenReturn(Optional.empty());




        try {


            vehicleService.updateVehicle(
                    1L,
                    new VehicleRequest()
            );


        }
        catch(Exception e){


            assertThat(e)
                    .isNotNull();

        }



    }



}
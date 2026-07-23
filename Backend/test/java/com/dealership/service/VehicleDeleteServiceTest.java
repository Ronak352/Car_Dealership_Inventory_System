package com.dealership.service;


import com.dealership.entity.Vehicle;
import com.dealership.repository.VehicleRepository;
import com.dealership.service.impl.VehicleServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.*;




class VehicleDeleteServiceTest {



    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private VehicleServiceImpl vehicleService;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }





    @Test
    void vehicleShouldBeDeletedSuccessfully(){



        Vehicle vehicle =

                Vehicle.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .build();



        when(
                vehicleRepository.findById(1L)
        )

        .thenReturn(
                Optional.of(vehicle)
        );




        vehicleService.deleteVehicle(1L);



        verify(vehicleRepository)
                .delete(vehicle);


    }







    @Test
    void deletingNonExistingVehicleShouldFail(){



        when(
                vehicleRepository.findById(1L)
        )

        .thenReturn(
                Optional.empty()
        );




        assertThatThrownBy(

                () -> vehicleService.deleteVehicle(1L)

        )

        .isInstanceOf(RuntimeException.class);



    }



}
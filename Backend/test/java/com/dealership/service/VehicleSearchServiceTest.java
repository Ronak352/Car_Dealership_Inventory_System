package com.dealership.service;


import com.dealership.dto.response.VehicleResponse;
import com.dealership.entity.Vehicle;

import com.dealership.enums.VehicleCategory;

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




class VehicleSearchServiceTest {



    @Mock
    private VehicleRepository vehicleRepository;



    @InjectMocks
    private VehicleServiceImpl vehicleService;





    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }







    @Test
    void vehiclesShouldBeSearchByBrand(){



        Vehicle vehicle =

                Vehicle.builder()

                .id(1L)

                .brand("Toyota")

                .model("Fortuner")

                .variant("Legender")

                .category(
                        VehicleCategory.SUV
                )

                .price(
                        new BigDecimal("4500000")
                )

                .build();





        when(
                vehicleRepository.searchVehicles(
                        "toyota",
                        null,
                        null,
                        null,
                        null
                )
        )

        .thenReturn(
                List.of(vehicle)
        );





        List<VehicleResponse> response =

                vehicleService.searchVehicles(
                        "Toyota",
                        null,
                        null,
                        null,
                        null
                );





        assertThat(response)
                .isNotEmpty();



        assertThat(response.get(0).getBrand())
                .isEqualTo("Toyota");





        verify(vehicleRepository)

                .searchVehicles(
                        "toyota",
                        null,
                        null,
                        null,
                        null
                );


    }








    @Test
    void searchWithPriceRangeShouldReturnVehicles(){



        when(

                vehicleRepository.searchVehicles(
                        null,
                        null,
                        null,
                        new BigDecimal("1000000"),
                        new BigDecimal("5000000")
                )

        )

        .thenReturn(List.of());





        List<VehicleResponse> response =

                vehicleService.searchVehicles(
                        null,
                        null,
                        null,
                        new BigDecimal("1000000"),
                        new BigDecimal("5000000")
                );




        assertThat(response)
                .isEmpty();




    }





}
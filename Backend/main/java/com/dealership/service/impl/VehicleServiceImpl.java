package com.dealership.service.impl;


import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.VehicleResponse;

import com.dealership.entity.Vehicle;

import com.dealership.exception.DuplicateResourceException;

import com.dealership.repository.VehicleRepository;

import com.dealership.service.VehicleService;

import java.util.List;

import java.util.stream.Collectors;

import com.dealership.enums.VehicleStatus;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import com.dealership.enums.VehicleCategory;

import java.math.BigDecimal;
import com.dealership.exception.ResourceNotFoundException;





@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {



    private final VehicleRepository vehicleRepository;




    @Override
    public VehicleResponse addVehicle(
            VehicleRequest request
    ){


        if(vehicleRepository.existsByVinNumber(
                request.getVinNumber()
        )){


            throw new DuplicateResourceException(
                    "Vehicle with VIN already exists"
            );

        }



        Vehicle vehicle = Vehicle.builder()


                .brand(request.getBrand())

                .model(request.getModel())

                .variant(request.getVariant())

                .category(request.getCategory())

                .fuelType(request.getFuelType())

                .transmission(request.getTransmission())

                .manufacturingYear(
                        request.getManufacturingYear()
                )

                .color(request.getColor())

                .engineNumber(
                        request.getEngineNumber()
                )

                .vinNumber(
                        request.getVinNumber()
                )

                .price(
                        request.getPrice()
                )

                .discount(
                        request.getDiscount()
                )

                .quantity(
                        request.getQuantity()
                )

                .condition(
                        request.getCondition()
                )

                .status(
                        request.getStatus()
                )

                .build();



        Vehicle savedVehicle =
                vehicleRepository.save(vehicle);



        return VehicleResponse.builder()

                .id(savedVehicle.getId())

                .brand(savedVehicle.getBrand())

                .model(savedVehicle.getModel())

                .variant(savedVehicle.getVariant())

                .vinNumber(savedVehicle.getVinNumber())

                .price(savedVehicle.getPrice())

                .quantity(savedVehicle.getQuantity())

                .build();


    }

    @Override
    public List<VehicleResponse> getAvailableVehicles() {


        return vehicleRepository
                .findByStatus(VehicleStatus.AVAILABLE)

                .stream()

                .map(vehicle -> VehicleResponse.builder()

                        .id(vehicle.getId())

                        .brand(vehicle.getBrand())

                        .model(vehicle.getModel())

                        .variant(vehicle.getVariant())

                        .vinNumber(vehicle.getVinNumber())

                        .price(vehicle.getPrice())

                        .quantity(vehicle.getQuantity())

                        .build()

                )

                .collect(Collectors.toList());

    }
    @Override
    public List<VehicleResponse> searchVehicles(

            String brand,

            String model,

            VehicleCategory category,

            BigDecimal minPrice,

            BigDecimal maxPrice

    ) {



        String searchBrand =
                brand != null
                        ? brand.toLowerCase()
                        : null;


        String searchModel =
                model != null
                        ? model.toLowerCase()
                        : null;



        return vehicleRepository.searchVehicles(

                searchBrand,

                searchModel,

                category,

                minPrice,

                maxPrice

        )

        .stream()

        .map(vehicle -> VehicleResponse.builder()

                .id(vehicle.getId())

                .brand(vehicle.getBrand())

                .model(vehicle.getModel())

                .variant(vehicle.getVariant())

                .vinNumber(vehicle.getVinNumber())

                .price(vehicle.getPrice())

                .quantity(vehicle.getQuantity())

                .build()

        )

        .collect(Collectors.toList());

    }
    
    @Override
    public VehicleResponse updateVehicle(

            Long id,

            VehicleRequest request

    ) {


        Vehicle vehicle =

                vehicleRepository.findById(id)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle not found with id: " + id
                        )
                );



        vehicle.setBrand(
                request.getBrand()
        );


        vehicle.setModel(
                request.getModel()
        );


        vehicle.setVariant(
                request.getVariant()
        );


        vehicle.setCategory(
                request.getCategory()
        );


        vehicle.setFuelType(
                request.getFuelType()
        );


        vehicle.setTransmission(
                request.getTransmission()
        );


        vehicle.setManufacturingYear(
                request.getManufacturingYear()
        );


        vehicle.setColor(
                request.getColor()
        );


        vehicle.setPrice(
                request.getPrice()
        );


        vehicle.setDiscount(
                request.getDiscount()
        );


        vehicle.setQuantity(
                request.getQuantity()
        );


        vehicle.setCondition(
                request.getCondition()
        );


        vehicle.setStatus(
                request.getStatus()
        );



        Vehicle updatedVehicle =

                vehicleRepository.save(vehicle);



        return VehicleResponse.builder()

                .id(updatedVehicle.getId())

                .brand(updatedVehicle.getBrand())

                .model(updatedVehicle.getModel())

                .variant(updatedVehicle.getVariant())

                .vinNumber(updatedVehicle.getVinNumber())

                .price(updatedVehicle.getPrice())

                .quantity(updatedVehicle.getQuantity())

                .build();

    }
    
    
    @Override
    public void deleteVehicle(Long id){

        Vehicle vehicle =
                vehicleRepository.findById(id)
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                        "Vehicle not found with id: " + id
                    )
                );


        vehicleRepository.delete(vehicle);

    }

}
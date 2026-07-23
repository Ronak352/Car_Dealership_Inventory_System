package com.dealership.service;


import com.dealership.dto.request.VehicleRequest;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.enums.VehicleCategory;


import java.math.BigDecimal;
import java.util.List;



public interface VehicleService {


    VehicleResponse addVehicle(
            VehicleRequest request
    );



    List<VehicleResponse> getAvailableVehicles();



    List<VehicleResponse> searchVehicles(

            String brand,

            String model,

            VehicleCategory category,

            BigDecimal minPrice,

            BigDecimal maxPrice

    );



    VehicleResponse updateVehicle(

            Long id,

            VehicleRequest request

    );
    
    void deleteVehicle(Long id);


}
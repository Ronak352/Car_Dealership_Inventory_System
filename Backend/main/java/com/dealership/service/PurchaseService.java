package com.dealership.service;


import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.PurchaseResponse;

import java.util.List;


public interface PurchaseService {


    PurchaseResponse createPurchase(
            PurchaseRequest request
    );

    List<PurchaseResponse> getPurchasesByCustomer(Long customerId);

    List<PurchaseResponse> getPurchasesByVehicle(Long vehicleId);

    List<PurchaseResponse> getPurchasesBySalesperson(Long salespersonId);

    PurchaseResponse getPurchaseById(Long id);

}

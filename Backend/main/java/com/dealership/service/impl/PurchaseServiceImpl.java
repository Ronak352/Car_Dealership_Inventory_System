package com.dealership.service.impl;


import com.dealership.dto.request.PurchaseRequest;
import com.dealership.dto.response.PurchaseResponse;

import com.dealership.entity.Customer;
import com.dealership.entity.PurchaseHistory;
import com.dealership.entity.Vehicle;

import com.dealership.enums.VehicleStatus;

import com.dealership.exception.ResourceNotFoundException;

import com.dealership.repository.CustomerRepository;
import com.dealership.repository.PurchaseRepository;
import com.dealership.repository.VehicleRepository;

import com.dealership.service.PurchaseService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {



    private final PurchaseRepository purchaseRepository;

    private final CustomerRepository customerRepository;

    private final VehicleRepository vehicleRepository;



    @Override
    public PurchaseResponse createPurchase(PurchaseRequest request) {


        Customer customer =
                customerRepository.findById(request.getCustomerId())

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer not found"
                        )
                );



        Vehicle vehicle =
                vehicleRepository.findById(request.getVehicleId())

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle not found"
                        )
                );



        if(vehicle.getQuantity() <= 0){

            throw new RuntimeException(
                    "Vehicle is out of stock"
            );

        }



        PurchaseHistory purchase =
                PurchaseHistory.builder()

                .customer(customer)

                .vehicle(vehicle)

                .purchaseDate(request.getPurchaseDate())

                .deliveryDate(request.getDeliveryDate())

                .sellingPrice(request.getSellingPrice())

                .paymentMethod(request.getPaymentMethod())

                .purchaseStatus(request.getPurchaseStatus())

                .build();



        PurchaseHistory savedPurchase =
                purchaseRepository.save(purchase);




        // Reduce vehicle quantity

        vehicle.setQuantity(
                vehicle.getQuantity() - 1
        );



        if(vehicle.getQuantity() == 0){

            vehicle.setStatus(
                    VehicleStatus.SOLD
            );

        }



        vehicleRepository.save(vehicle);



        return mapToResponse(savedPurchase);

    }





    @Override
    public List<PurchaseResponse> getPurchasesByCustomer(Long customerId) {


        return purchaseRepository
                .findByCustomerId(customerId)

                .stream()

                .map(this::mapToResponse)

                .collect(Collectors.toList());

    }





    @Override
    public List<PurchaseResponse> getPurchasesByVehicle(Long vehicleId) {


        return purchaseRepository
                .findByVehicleId(vehicleId)

                .stream()

                .map(this::mapToResponse)

                .collect(Collectors.toList());

    }





    @Override
    public List<PurchaseResponse> getPurchasesBySalesperson(Long salespersonId) {


        return purchaseRepository
                .findBySalespersonId(salespersonId)

                .stream()

                .map(this::mapToResponse)

                .collect(Collectors.toList());

    }





    @Override
    public PurchaseResponse getPurchaseById(Long id) {


        PurchaseHistory purchase =
                purchaseRepository.findById(id)

                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Purchase not found"
                        )
                );


        return mapToResponse(purchase);

    }





    private PurchaseResponse mapToResponse(
            PurchaseHistory purchase
    ){


        return PurchaseResponse.builder()

                .id(purchase.getId())

                .customerName(
                        purchase.getCustomer()
                        .getUser()
                        .getFirstName()
                        +
                        " "
                        +
                        purchase.getCustomer()
                        .getUser()
                        .getLastName()
                )

                .vehicleName(
                        purchase.getVehicle()
                        .getBrand()
                        +
                        " "
                        +
                        purchase.getVehicle()
                        .getModel()
                )

                .purchaseDate(
                        purchase.getPurchaseDate()
                )

                .deliveryDate(
                        purchase.getDeliveryDate()
                )

                .sellingPrice(
                        purchase.getSellingPrice()
                )

                .paymentMethod(
                        purchase.getPaymentMethod()
                )

                .purchaseStatus(
                        purchase.getPurchaseStatus()
                )

                .build();

    }

}
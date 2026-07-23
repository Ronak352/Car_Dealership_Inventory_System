package com.dealership.service.impl;


import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.response.CustomerResponse;

import com.dealership.entity.Customer;
import com.dealership.entity.User;

import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;

import com.dealership.repository.CustomerRepository;
import com.dealership.repository.UserRepository;

import com.dealership.service.CustomerService;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;



@Service
@RequiredArgsConstructor
public class CustomerServiceImpl 
        implements CustomerService {



    private final CustomerRepository customerRepository;

    private final UserRepository userRepository;




    @Override
    public CustomerResponse createCustomer(
            Long userId,
            CustomerRequest request) {


        User user =
            userRepository.findById(userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                "User not found with id : "
                + userId));



        if(customerRepository.findByUserId(userId)
                .isPresent()){


            throw new DuplicateResourceException(
            "Customer already exists");

        }



        Customer customer =
                Customer.builder()
                .user(user)
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();



        return mapToResponse(
            customerRepository.save(customer)
        );

    }




    @Override
    public CustomerResponse getCustomerById(Long id){


        return mapToResponse(

            customerRepository.findById(id)

            .orElseThrow(() ->
                new ResourceNotFoundException(
                "Customer not found"))
        );

    }




    @Override
    public CustomerResponse getCustomerByUserId(Long userId){


        return mapToResponse(

            customerRepository.findByUserId(userId)

            .orElseThrow(() ->
                new ResourceNotFoundException(
                "Customer not found"))
        );

    }




    @Override
    public List<CustomerResponse> getAllCustomers(){


        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }




    @Override
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request){



        Customer customer =
            customerRepository.findById(id)

            .orElseThrow(() ->
                new ResourceNotFoundException(
                "Customer not found"));



        customer.setAddress(
                request.getAddress());

        customer.setCity(
                request.getCity());

        customer.setState(
                request.getState());

        customer.setPincode(
                request.getPincode());



        return mapToResponse(
                customerRepository.save(customer)
        );

    }





    @Override
    public void deleteCustomer(Long id){


        Customer customer =
            customerRepository.findById(id)

            .orElseThrow(() ->
                new ResourceNotFoundException(
                "Customer not found"));



        customerRepository.delete(customer);

    }





    private CustomerResponse mapToResponse(
            Customer customer){


        User user = customer.getUser();


        return CustomerResponse.builder()

                .id(customer.getId())

                .fullName(
                    user.getFirstName()
                    +" "
                    +user.getLastName()
                )

                .email(user.getEmail())

                .phone(user.getPhone())

                .address(customer.getAddress())

                .build();

    }

}
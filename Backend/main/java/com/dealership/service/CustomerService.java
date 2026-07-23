package com.dealership.service;


import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.response.CustomerResponse;


import java.util.List;



public interface CustomerService {


    CustomerResponse createCustomer(
            Long userId,
            CustomerRequest request
    );



    CustomerResponse getCustomerById(Long id);



    CustomerResponse getCustomerByUserId(Long userId);



    List<CustomerResponse> getAllCustomers();



    CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request
    );



    void deleteCustomer(Long id);

}
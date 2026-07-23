package com.dealership.service;


import com.dealership.dto.request.CustomerRequest;
import com.dealership.dto.response.CustomerResponse;
import com.dealership.entity.Customer;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.CustomerRepository;
import com.dealership.repository.UserRepository;
import com.dealership.service.impl.CustomerServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;



class CustomerServiceTest {



    @Mock
    private CustomerRepository customerRepository;



    @Mock
    private UserRepository userRepository;



    @InjectMocks
    private CustomerServiceImpl customerService;




    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }






    @Test
    void shouldCreateCustomerSuccessfully(){



        CustomerRequest request =
                CustomerRequest.builder()
                .address("SG Highway")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();




        User user =
                User.builder()
                .id(1L)
                .firstName("Ronak")
                .lastName("Rathod")
                .email("ronak@gmail.com")
                .phone("9999999999")
                .role(Role.CUSTOMER)
                .password("123")
                .build();




        Customer customer =
                Customer.builder()
                .id(1L)
                .user(user)
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();




        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));



        when(customerRepository.findByUserId(1L))
                .thenReturn(Optional.empty());



        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);




        CustomerResponse response =
                customerService.createCustomer(
                        1L,
                        request
                );



        assertThat(response).isNotNull();

        assertThat(response.getEmail())
                .isEqualTo("ronak@gmail.com");


        verify(customerRepository)
                .save(any(Customer.class));

    }







    @Test
    void shouldThrowDuplicateCustomerException(){



        CustomerRequest request =
                CustomerRequest.builder()
                .address("Ahmedabad")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();




        when(userRepository.findById(1L))
                .thenReturn(Optional.of(
                        User.builder()
                        .id(1L)
                        .build()
                ));



        when(customerRepository.findByUserId(1L))
                .thenReturn(
                    Optional.of(
                        Customer.builder()
                        .id(1L)
                        .build()
                    )
                );



        assertThatThrownBy(() ->
                customerService.createCustomer(
                        1L,
                        request
                ))

                .isInstanceOf(
                    DuplicateResourceException.class
                );


        verify(customerRepository,never())
                .save(any(Customer.class));

    }







    @Test
    void shouldThrowUserNotFoundException(){



        CustomerRequest request =
                CustomerRequest.builder()
                .address("Address")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();




        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());




        assertThatThrownBy(() ->
                customerService.createCustomer(
                        1L,
                        request
                ))

                .isInstanceOf(
                    ResourceNotFoundException.class
                );

    }







    @Test
    void shouldGetCustomerById(){



        User user =
                User.builder()
                .firstName("Ronak")
                .lastName("Rathod")
                .email("ronak@gmail.com")
                .phone("9999999999")
                .build();



        Customer customer =
                Customer.builder()
                .id(1L)
                .user(user)
                .address("Ahmedabad")
                .build();




        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));




        CustomerResponse response =
                customerService.getCustomerById(1L);



        assertThat(response)
                .isNotNull();


        assertThat(response.getId())
                .isEqualTo(1L);

    }








    @Test
    void shouldReturnAllCustomers(){



        Customer customer =
                Customer.builder()
                .id(1L)
                .user(
                    User.builder()
                    .firstName("Ronak")
                    .lastName("Rathod")
                    .email("ronak@gmail.com")
                    .build()
                )
                .build();



        when(customerRepository.findAll())
                .thenReturn(
                    List.of(customer)
                );




        List<CustomerResponse> customers =
                customerService.getAllCustomers();




        assertThat(customers)
                .hasSize(1);

    }








    @Test
    void shouldUpdateCustomer(){



        Customer customer =
                Customer.builder()
                .id(1L)
                .user(
                    User.builder()
                    .email("ronak@gmail.com")
                    .firstName("Ronak")
                    .lastName("Rathod")
                    .build()
                )
                .address("Old Address")
                .build();




        CustomerRequest request =
                CustomerRequest.builder()
                .address("New Address")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380054")
                .build();



        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));



        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);




        CustomerResponse response =
                customerService.updateCustomer(
                        1L,
                        request
                );



        assertThat(response.getAddress())
                .isEqualTo("New Address");

    }







    @Test
    void shouldDeleteCustomer(){



        Customer customer =
                Customer.builder()
                .id(1L)
                .build();




        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));




        customerService.deleteCustomer(1L);



        verify(customerRepository)
                .delete(customer);

    }




}
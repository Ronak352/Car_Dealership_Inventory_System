package com.dealership.repository;

import com.dealership.entity.Customer;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_returnsCustomer() {

        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("Ronak")
                        .lastName("Rathod")
                        .email("ronak@test.com")
                        .password("12345")
                        .role(Role.CUSTOMER)
                        .build()
        );

        Customer customer = Customer.builder()
                .user(user)
                .address("Satellite")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380015")
                .build();

        entityManager.persistAndFlush(customer);

        Optional<Customer> result =
                customerRepository.findByUserId(user.getId());

        assertThat(result).isPresent();

        assertThat(result.get().getUser().getId())
                .isEqualTo(user.getId());

        assertThat(result.get().getCity())
                .isEqualTo("Ahmedabad");

        assertThat(result.get().getState())
                .isEqualTo("Gujarat");
    }

    @Test
    void findAll_returnsCustomers() {

        User user = entityManager.persistAndFlush(
                User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@test.com")
                        .password("12345")
                        .role(Role.CUSTOMER)
                        .build()
        );

        entityManager.persistAndFlush(
                Customer.builder()
                        .user(user)
                        .address("Navrangpura")
                        .city("Ahmedabad")
                        .state("Gujarat")
                        .pincode("380009")
                        .build()
        );

        assertThat(customerRepository.findAll())
                .isNotEmpty();
    }
}
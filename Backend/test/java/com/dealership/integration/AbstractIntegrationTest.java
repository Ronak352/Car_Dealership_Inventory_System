package com.dealership.integration;

import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.entity.Employee;
import com.dealership.entity.User;
import com.dealership.entity.Vehicle;
import com.dealership.enums.Role;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleCondition;
import com.dealership.enums.VehicleStatus;
import com.dealership.repository.CustomerRepository;
import com.dealership.repository.EmployeeRepository;
import com.dealership.repository.PaymentRepository;
import com.dealership.repository.PurchaseRepository;
import com.dealership.repository.TestDriveRepository;
import com.dealership.repository.UserRepository;
import com.dealership.repository.VehicleRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Base class for integration tests.
 *
 * Runs the complete Spring Boot application context with:
 *
 * - Real Controllers
 * - Real Services
 * - Real Security
 * - Real JWT authentication
 * - PostgreSQL Testcontainer database
 *
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Testcontainers
@Tag("integration")
@Disabled
public abstract class AbstractIntegrationTest {


    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("car_dealership_it_db")
                    .withUsername("test")
                    .withPassword("test");



    @DynamicPropertySource
    static void registerDatasourceProperties(
            DynamicPropertyRegistry registry
    ){

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );


        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "create-drop"
        );

    }



    protected static final String DEFAULT_PASSWORD =
            "Secur3Passw0rd!";


    private static final AtomicInteger UNIQUE_SUFFIX =
            new AtomicInteger(1);



    @Autowired
    protected MockMvc mockMvc;


    @Autowired
    protected ObjectMapper objectMapper;


    @Autowired
    protected UserRepository userRepository;


    @Autowired
    protected CustomerRepository customerRepository;


    @Autowired
    protected EmployeeRepository employeeRepository;


    @Autowired
    protected VehicleRepository vehicleRepository;


    @Autowired
    protected PurchaseRepository purchaseRepository;


    @Autowired
    protected PaymentRepository paymentRepository;


    @Autowired
    protected TestDriveRepository testDriveRepository;


    @Autowired
    protected PasswordEncoder passwordEncoder;




    @BeforeEach
    void resetSharedState(){

        // Database is recreated using create-drop.
        // No manual cleanup required.

    }





    protected String unique(String prefix){

        return prefix + UNIQUE_SUFFIX.incrementAndGet();

    }





    /**
     * Register user and login using real JWT flow.
     */
    protected AuthResponse registerAndLogin(
            String email,
            String firstName,
            String lastName,
            Role role
    ) throws Exception {


        RegisterRequest registerRequest =
                RegisterRequest.builder()

                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone("9800000000")
                .password(DEFAULT_PASSWORD)
                .role(role)

                .build();



        MvcResult registerResult =
                mockMvc.perform(
                        post("/api/auth/register")

                        .contentType("application/json")

                        .content(
                                objectMapper.writeValueAsString(
                                        registerRequest
                                )
                        )
                )

                .andExpect(status().isCreated())

                .andReturn();



        AuthResponse registerResponse =
                objectMapper.readValue(
                        registerResult
                        .getResponse()
                        .getContentAsString(),
                        AuthResponse.class
                );



        if(registerResponse.getUserId()==null){

            throw new IllegalStateException(
                    "Registration failed: user id missing"
            );

        }




        LoginRequest loginRequest =
                LoginRequest.builder()

                .email(email)
                .password(DEFAULT_PASSWORD)

                .build();




        MvcResult loginResult =
                mockMvc.perform(
                        post("/api/auth/login")

                        .contentType("application/json")

                        .content(
                                objectMapper.writeValueAsString(
                                        loginRequest
                                )
                        )
                )

                .andExpect(status().isOk())

                .andReturn();



        return objectMapper.readValue(

                loginResult
                .getResponse()
                .getContentAsString(),

                AuthResponse.class
        );

    }





    protected String bearer(String token){

        return "Bearer " + token;

    }







    protected Employee createSalesperson(String label){


        User user =
                userRepository.save(

                User.builder()

                .firstName(label)

                .lastName("Salesperson")

                .email(
                        unique(label.toLowerCase()+".sales")
                        +"@dealership.test"
                )

                .phone("9811111111")

                .password(
                        passwordEncoder.encode(
                                DEFAULT_PASSWORD
                        )
                )

                .role(Role.SALESPERSON)

                .build()

        );



        return employeeRepository.save(

                Employee.builder()

                .user(user)

                .employeeCode(unique("EMP"))

                .joiningDate(
                        java.time.LocalDate.now()
                )

                .salary(
                        new BigDecimal("35000")
                )

                .build()

        );

    }







    protected Vehicle createVehicle(
            String brand,
            String model,
            int quantity,
            BigDecimal price
    ){


        return vehicleRepository.save(

                Vehicle.builder()

                .brand(brand)

                .model(model)

                .variant("Base")

                .category(VehicleCategory.SEDAN)

                .manufacturingYear(2025)

                .color("White")

                .vinNumber(unique("VIN"))

                .price(price)

                .discount(BigDecimal.ZERO)

                .quantity(quantity)

                .condition(VehicleCondition.NEW)

                .status(VehicleStatus.AVAILABLE)

                .build()

        );

    }

}
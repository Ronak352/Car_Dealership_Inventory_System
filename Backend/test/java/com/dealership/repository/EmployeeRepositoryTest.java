package com.dealership.repository;


import com.dealership.entity.Employee;
import com.dealership.entity.User;

import com.dealership.enums.Role;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.math.BigDecimal;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;



class EmployeeRepositoryTest extends AbstractRepositoryTest {



    @Autowired
    private EmployeeRepository employeeRepository;



    @Autowired
    private TestEntityManager entityManager;







    @Test
    void findByEmployeeCode_returnsEmployee() {



        User user =
                entityManager.persistAndFlush(
                        User.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .email("john.employee@test.com")
                                .password("12345")
                                .role(Role.SALESPERSON)
                                .build()
                );



        Employee employee =
                Employee.builder()
                        .user(user)
                        .employeeCode("EMP001")
                        .salary(new BigDecimal("50000"))
                        .build();



        entityManager.persistAndFlush(employee);



        var result =
                employeeRepository.findByEmployeeCode("EMP001");



        assertThat(result)
                .isPresent();



        assertThat(result.get().getEmployeeCode())
                .isEqualTo("EMP001");

    }








    @Test
    void findByUserId_returnsEmployee() {



        User user =
                entityManager.persistAndFlush(
                        User.builder()
                                .firstName("Alex")
                                .lastName("Brown")
                                .email("alex.employee@test.com")
                                .password("12345")
                                .role(Role.MANAGER)
                                .build()
                );



        Employee employee =
                Employee.builder()
                        .user(user)
                        .employeeCode("EMP002")
                        .salary(new BigDecimal("70000"))
                        .build();



        entityManager.persistAndFlush(employee);



        var result =
                employeeRepository.findByUserId(user.getId());



        assertThat(result)
                .isPresent();



        assertThat(result.get().getUser().getId())
                .isEqualTo(user.getId());

    }









    @Test
    void existsByEmployeeCode_returnsTrue() {



        User user =
                entityManager.persistAndFlush(
                        User.builder()
                                .firstName("David")
                                .lastName("Wilson")
                                .email("david.employee@test.com")
                                .password("12345")
                                .role(Role.SALESPERSON)
                                .build()
                );



        Employee employee =
                Employee.builder()
                        .user(user)
                        .employeeCode("EMP003")
                        .salary(new BigDecimal("45000"))
                        .build();



        entityManager.persistAndFlush(employee);



        boolean exists =
                employeeRepository.existsByEmployeeCode("EMP003");



        assertThat(exists)
                .isTrue();

    }









    @Test
    void searchEmployees_returnsMatchingEmployees() {



        User user =
                entityManager.persistAndFlush(
                        User.builder()
                                .firstName("Robert")
                                .lastName("Miller")
                                .email("robert.employee@test.com")
                                .password("12345")
                                .role(Role.SALESPERSON)
                                .build()
                );



        Employee employee =
                Employee.builder()
                        .user(user)
                        .employeeCode("SALES001")
                        .salary(new BigDecimal("40000"))
                        .build();



        entityManager.persistAndFlush(employee);



        List<Employee> result =
                employeeRepository
                .findByEmployeeCodeContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                        "sales",
                        "sales",
                        "sales",
                        "sales"
                );



        assertThat(result)
                .hasSize(1);



        assertThat(result.get(0).getEmployeeCode())
                .isEqualTo("SALES001");

    }








    @Test
    void findAll_returnsEmployees() {



        List<Employee> result =
                employeeRepository.findAll();



        assertThat(result)
                .isNotNull();

    }

}
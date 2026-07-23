package com.dealership.dto.request;

import com.dealership.enums.Role;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateEmployeeRequest() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailWhenEmployeeCodeIsBlank() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void shouldFailWhenUserIdIsNull() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void shouldFailWhenJoiningDateIsNull() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .salary(BigDecimal.valueOf(50000))
                .role(Role.SALESPERSON)
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void shouldFailWhenSalaryIsNull() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .role(Role.SALESPERSON)
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void shouldFailWhenRoleIsNull() {

        EmployeeRequest dto = EmployeeRequest.builder()
                .userId(1L)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .build();

        assertThat(validator.validate(dto)).isNotEmpty();
    }
}
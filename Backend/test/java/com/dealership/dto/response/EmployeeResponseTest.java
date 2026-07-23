package com.dealership.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeResponseTest {

    @Test
    void shouldCreateEmployeeResponse() {

        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .userId(2L)
                .employeeCode("EMP001")
                .fullName("John Smith")
                .email("john@gmail.com")
                .phone("9999999999")
                .joiningDate(LocalDate.of(2025, 1, 1))
                .salary(BigDecimal.valueOf(50000))
                .role("SALESPERSON")
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getEmployeeCode()).isEqualTo("EMP001");
        assertThat(response.getFullName()).isEqualTo("John Smith");
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        assertThat(response.getPhone()).isEqualTo("9999999999");
        assertThat(response.getJoiningDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(response.getSalary()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(response.getRole()).isEqualTo("SALESPERSON");
    }
}
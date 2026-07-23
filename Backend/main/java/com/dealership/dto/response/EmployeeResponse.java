package com.dealership.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;

    private Long userId;

    private String employeeCode;

    private String fullName;

    private String email;

    private String phone;

    private LocalDate joiningDate;

    private BigDecimal salary;

    private String role;
}
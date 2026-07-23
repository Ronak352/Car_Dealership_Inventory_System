package com.dealership.dto.request;

import com.dealership.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotNull(message = "User Id is required")
    private Long userId;

    @NotBlank(message = "Employee Code is required")
    private String employeeCode;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Salary is required")
    private BigDecimal salary;

    @NotNull(message = "Role is required")
    private Role role;
}
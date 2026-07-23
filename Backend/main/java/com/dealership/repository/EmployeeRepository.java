package com.dealership.repository;

import com.dealership.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByUserId(Long userId);

    boolean existsByEmployeeCode(String employeeCode);

    List<Employee> findByEmployeeCodeContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
            String employeeCode,
            String firstName,
            String lastName,
            String email
    );
}
package com.dealership.repository;

import com.dealership.entity.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanDetails, Long> {

    Optional<LoanDetails> findByPurchaseId(Long purchaseId);
}

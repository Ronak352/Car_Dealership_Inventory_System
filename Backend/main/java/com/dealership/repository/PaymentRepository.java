package com.dealership.repository;


import com.dealership.entity.PaymentHistory;
import com.dealership.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;



@Repository
public interface PaymentRepository 
        extends JpaRepository<PaymentHistory, Long> {



    List<PaymentHistory> findByPurchaseId(Long purchaseId);



    List<PaymentHistory> findByPaymentStatus(PaymentStatus paymentStatus);



    Optional<PaymentHistory> findByTransactionId(String transactionId);



    boolean existsByTransactionId(String transactionId);

}